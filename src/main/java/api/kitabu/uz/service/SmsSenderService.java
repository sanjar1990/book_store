package api.kitabu.uz.service;

import api.kitabu.uz.dto.auth.SmsDTO;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.SmsType;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Random;

@Slf4j
@Service
public class SmsSenderService {
    private final String url = "https://api.dasturjon.uz/api/v1/sms-provider/send";
    private final int smsLimitCount = 3;
    private final Random rnd = new Random();
    @Autowired
    public SmsHistoryService smsHistoryService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ResourceBundleService resourceBundleService;

    // Do not change sms patterns. Other patters will not work!
//    private final String confirmTextUz = "<#>kitabu.uz ro‘yxatdan o‘tish tasdiqlash kodi: "; // + signature
    private final String registrationTextUz = "<#>kitabu.uz partali. Ro'yxatdan o'tish uchun tasdiqlash kodi: "; // + signature
    private final String changePasswordTextUz = "<#>kitabu.uz partali. Parolni o'zgartirish uchun tasdiqlash kodi: "; // + signature
    private final String changePhoneTextUz = "<#>kitabu.uz partali. Telefon raqamni o'zgartirish uchun tasdiqlash kodi: "; // + signature
    private final String confirmTextRu = "<#>Портала kitabu.uz. Код для подтверждения регистрации: "; // + signature

    public void sendRegistrationSms(String phone, String signature, AppLanguage language) {
        if (smsHistoryService.getSendLimitCountLast2Minute(phone) >= smsLimitCount) {
            log.info("Sms limit reached phone {}", phone);
            throw new APIException(resourceBundleService.getMessage("sms.limit.reached ", language.name()) + phone);
        }
        String message;
        if (language.equals(AppLanguage.ru)) {
            message = confirmTextRu;
        } else {
            message = registrationTextUz;
        }
        String smsCode = generateSmsCode();
        if (signature == null) {
            signature = "";
        }
        message = message + smsCode + "\n" + signature;
        // send sms code
        send(phone, message, smsCode);
        // save
        smsHistoryService.save(phone, smsCode, message, SmsType.REGISTRATION);
    }

    public void sendResetPswd(String phone, String signature) {
        String smsCode = generateSmsCode();
        if (signature == null) {
            signature = "";
        }
        String message = changePasswordTextUz + smsCode + "\n" + signature;
        // send sms code
        send(phone, message, smsCode);
        // save
        smsHistoryService.save(phone, smsCode, message, SmsType.RESET_PASSWORD);
    }

    public void reSendSms(String phone, String signature, AppLanguage language) {
        String message;
        if (language.equals(AppLanguage.ru)) {
            message = confirmTextRu;
        } else {
            message = registrationTextUz;
        }
        String smsCode = generateSmsCode();
        if (signature == null) {
            signature = null + " ";
        }
        message = message + smsCode + "\n" + signature;
        // send sms code
        send(phone, message, smsCode);
        smsHistoryService.save(phone, smsCode, message, SmsType.RESEND_SMS);

    }

    public void sendChangePhoneSms(String phone, String signature) {
        String smsCode = generateSmsCode();
        if (signature == null) {
            signature = null + " ";
        }
        String message = changePhoneTextUz + smsCode + "\n" + signature;
        // send sms code
        send(phone, message, smsCode);
        // save
        smsHistoryService.save(phone, smsCode, message, SmsType.UPDATE_PHONE);
    }

    private void send(String phone, String message, String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // count
        SmsDTO smsDTO = new SmsDTO();
        smsDTO.setPhone(phone);
        smsDTO.setMessage(message);
        smsDTO.setProjectName("kitobjon2");
        smsDTO.setCode(code);
        // body
        HttpEntity<SmsDTO> entitySms = new HttpEntity<>(smsDTO, headers);
        try {
            log.info("-------------------------------------------------------------------");
            log.info("Send SMS to phone: {},  message: {} ", smsDTO.getPhone(), smsDTO.getMessage());
            String answer = restTemplate.postForObject(url, entitySms, String.class);
            log.info("Result SMS to phone: {}, answer {} ", smsDTO.getPhone(), answer);
            log.info("-------------------------------------------------------------------");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String generateSmsCode() {
        Integer n = 10000 + rnd.nextInt(90000);
        return n.toString();
    }
}
