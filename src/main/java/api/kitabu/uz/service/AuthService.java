package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.auth.*;
import api.kitabu.uz.entity.AttachEntity;
import api.kitabu.uz.entity.ProfileEntity;
import api.kitabu.uz.enums.*;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.AttachRepository;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.util.JwtTokenUtil;
import api.kitabu.uz.util.MD5util;
import api.kitabu.uz.util.PhoneUtil;
import api.kitabu.uz.util.SpringSecurityUtil;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;

@Service
@Log4j2
public class AuthService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private SmsSenderService smsSenderService;
    @Autowired
    private AttachRepository attachRepository;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ResourceBundleMessageSource messageSource;
    @Autowired
    private AttachService attachService;

    public ApiResponse<AuthorizationResponseDTO> authorization(AuthorizationDTO dto, Lang lang) {
        String phone = PhoneUtil.correctPhone(dto.getPhone());
        Optional<ProfileEntity> byLoginAndPassword = profileRepository
                .findByPhoneAndPasswordAndVisible(phone, new BCryptPasswordEncoder().encode(dto.getPassword()), true);
        if (byLoginAndPassword.isEmpty())
            throw new APIException(messageSource.getMessage("profile.not.found", null, new Locale(lang.name())));
        if (!byLoginAndPassword.get().getStatus().equals(GeneralStatus.ACTIVE))
            throw new APIException(messageSource.getMessage("profile.is.blocked", null, new Locale(lang.name())));
        return ApiResponse.ok(getClientAuthorizationResponse(byLoginAndPassword.get()));
    }

    /**
     * Registration
     */
    public ApiResponse<?> registration(RegistrationUserDTO dto, AppLanguage lang) {
        String phone = PhoneUtil.correctPhone(dto.getPhone());
        Optional<ProfileEntity> profile = profileRepository.findByPhoneAndVisible(phone, true);
        if (profile.isPresent() && profile.get().getStatus().equals(GeneralStatus.ACTIVE))
            throw new APIException(messageSource.getMessage("profile.already.exists", null, new Locale(lang.name())));
        if (profile.isPresent() && profile.get().getStatus().equals(GeneralStatus.NOT_ACTIVE)){
            profileRoleService.deleteProfile(profile.get().getId());
            profileRepository.deleteProfile(profile.get().getId());
        }

        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhone(phone);
        entity.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        entity.setAppLanguage(lang);
        entity.setSignature(dto.getSignature());
        if (dto.getPhotoId() != null) {
            Optional<AttachEntity> byId = attachRepository.findById(dto.getPhotoId());
            if (byId.isPresent())
                entity.setPhotoId(dto.getPhotoId());
        }
        entity.setStatus(GeneralStatus.NOT_ACTIVE);
        // save profile
        profileRepository.save(entity);

        // create roles in a case of visible false
        profileRoleService.createForNotFullyReg(entity.getId(), ProfileRole.ROLE_USER);
        // send sms
        smsSenderService.sendRegistrationSms(entity.getPhone(), entity.getSignature(), lang);
        return ApiResponse.ok();
    }


    public ApiResponse<AuthorizationResponseDTO> registrationVerification(SmsVerificationDTO dto, AppLanguage appLanguage) {
        String phone = PhoneUtil.correctPhone(dto.getPhone());
        Optional<ProfileEntity> optional = profileRepository.findByPhoneAndVisible(phone, Boolean.TRUE);
        if (optional.isEmpty()) {
            log.warn("Profile not found! phone = {}", phone);
            throw new APIException(messageSource.getMessage("profile.not.found", null, new Locale(appLanguage.name())));
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.NOT_ACTIVE)) {
            log.warn("Profile Status Blocked! Phone = {}", phone);
            throw new APIException(messageSource.getMessage("profile.is.blocked", null, new Locale(appLanguage.name())));
        }
        // check sms code
        smsHistoryService.checkSmsCode(phone, dto.getCode(), appLanguage);
        // change client status
        profileRepository.updateStatus(profile.getId(), GeneralStatus.ACTIVE);
        // change the visibilities to true for profile roles
        profileRoleService.updateVisible(profile.getId(), true);

        return ApiResponse.ok(getClientAuthorizationResponse(profile));
    }

    /**
     * Reset Password
     */
    public ApiResponse<String> resetPasswordRequest(AuthResetProfileDTO dto, AppLanguage appLanguage) {
        String phone = PhoneUtil.correctPhone(dto.getPhone());
        Optional<ProfileEntity> optional = profileRepository.findByPhoneAndVisible(phone, Boolean.TRUE);
        if (optional.isEmpty()) {
            log.warn("Profile not found! phone = {}", phone);
            return new ApiResponse<>(messageSource.getMessage("profile.not.found", null, new Locale(appLanguage.name())), 400, true);
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Profile Status Blocked! Phone = {}", phone);
            return new ApiResponse<>(messageSource.getMessage("profile.is.blocked", null, new Locale(appLanguage.name())), 400, true);
        }
        // send sms
        smsSenderService.sendResetPswd(phone, dto.getSignature());
        return ApiResponse.ok();
    }

    public ApiResponse<AuthorizationResponseDTO> resetPasswordConfirm(ResetPasswordConfirmDTO dto, AppLanguage language) {
        String phone = PhoneUtil.correctPhone(dto.getPhone());
        Optional<ProfileEntity> optional = profileRepository.findByPhoneAndVisible(phone, Boolean.TRUE);
        // check phone number;
        if (optional.isEmpty()) {
            log.warn("Profile not found! phone = {}", phone);
            return new ApiResponse<>(messageSource.getMessage("profile.not.found", null, new Locale(language.name())), 400, true);
        }
        // check status of profile
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Profile Status Blocked! Phone = {}", phone);
            return new ApiResponse<>(messageSource.getMessage("profile.is.blocked", null, new Locale(language.name())), 400, true);
        }
        // check new password with repeat new password
        if (!dto.getNewPassword().equals(dto.getRepeatNewPassword())) {
            log.warn("New password is not same with  Repeated new password {} ", dto.getNewPassword(), dto.getRepeatNewPassword());
            return new ApiResponse<>(messageSource.getMessage("profile.is.blocked", null, new Locale(language.name())), 400, true);
        }

        // check sms code
        smsHistoryService.checkSmsCode(phone, dto.getCode(), language);
        // update password
        profileRepository.updatePassword(profile.getId(), MD5util.encode(dto.getNewPassword()));
        return new ApiResponse<>(200, false, getClientAuthorizationResponse(profile));
    }

    public AuthorizationResponseDTO getClientAuthorizationResponse(ProfileEntity profile) {
        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO();
        responseDTO.setId(profile.getId());
        responseDTO.setName(profile.getName());
        responseDTO.setSurname(profile.getSurname());
        responseDTO.setRoles(profileRoleService.getByProfileId(profile.getId()));
        responseDTO.setPhone(profile.getPhone());
        if (profile.getPhotoId() != null) {
            responseDTO.setPhoto(attachService.toDTO(profile.getPhotoId()));
        }
        responseDTO.setToken(JwtTokenUtil.encode(profile.getPhone(), profileRoleService.getByProfileId(profile.getId())));
        return responseDTO;
    }

    public ApiResponse<?> resendSms(String phoneNumber, AppLanguage language) {
        phoneNumber = PhoneUtil.correctPhone(phoneNumber);
        Optional<ProfileEntity> optional = profileRepository.findByPhoneAndVisible(phoneNumber, Boolean.TRUE);
        // check phone number;
        if (optional.isEmpty()) {
            log.warn("Profile not found! phone = {}", phoneNumber);
            return new ApiResponse<>(messageSource.getMessage("profile.not.found", null, new Locale(language.name())), 400, true);
        }

        SmsType smstype = smsHistoryService.getLastSmsByPhone(phoneNumber).get().getSmsType();
        GeneralStatus profileStatus = optional.get().getStatus();

        if (!profileStatus.equals(GeneralStatus.NOT_ACTIVE) && smstype.equals(SmsType.REGISTRATION)) {
            return new ApiResponse<>(400, true);
        }

        smsSenderService.reSendSms(phoneNumber, optional.get().getSignature(), language);
        return new ApiResponse<>(200, false);

    }

}
