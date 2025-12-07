package api.kitabu.uz.service;

import api.kitabu.uz.entity.SmsHistoryEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.SmsStatus;
import api.kitabu.uz.enums.SmsType;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.SmsHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
public class SmsHistoryService {

    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBundleService resourceBundleService;

    public void save(String phone, String code, String message, SmsType type) {
        SmsHistoryEntity history = new SmsHistoryEntity();
        history.setPhone(phone);
        history.setSmsCode(code);
        history.setSmsText(message);
        history.setSmsType(type);
        history.setStatus(SmsStatus.SEND);
        smsHistoryRepository.save(history);
    }

    public Long getSendLimitCountLast2Minute(String phone) {
        return smsHistoryRepository.countByPhoneAndCreatedDateBetween(phone,
                LocalDateTime.now().minusMinutes(2),
                LocalDateTime.now());
    }

    public Boolean checkSmsCode(String phone, String code, AppLanguage language) {
        Optional<SmsHistoryEntity> optional = getLastSmsByPhone(phone);
        /* it will take last message which was sent to phone.
        *   findTop means that limit 1 */
        if (optional.isEmpty()) {
            log.warn("Phone Incorrect! Phone = {}, code = {}", phone, code);
            throw new APIException(resourceBundleService.getMessage("sms.code.wrong", language.name()));
        }
        SmsHistoryEntity entity = optional.get();
        if (entity.getCreatedDate().plusMinutes(2L).isBefore(LocalDateTime.now())) {
            log.warn("Sms verification time out = {}, code = {}", phone, code);
            smsHistoryRepository.updateStatus(entity.getId(), SmsStatus.USED_WITH_TIMEOUT);
            throw new APIException(resourceBundleService.getMessage("sms.verification.time.out", language.name()));
        }
        if (!entity.getSmsCode().equals(code)) {
            throw new APIException(resourceBundleService.getMessage("sms.code.wrong", language.name()));
        }
        smsHistoryRepository.updateStatus(entity.getId(), SmsStatus.USED);
        return true;
    }

    public Optional<SmsHistoryEntity> getLastSmsByPhone(String phone){
        /* it will take last message which was sent to phone.
         *   findTop means that limit 1 */
       return  smsHistoryRepository.findTopByPhoneAndVisibleOrderByCreatedDateDesc(phone, true);
    }



}
