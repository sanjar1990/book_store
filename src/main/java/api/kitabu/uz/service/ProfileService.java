package api.kitabu.uz.service;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.user.UserFilterRequest;
import api.kitabu.uz.dto.filter.user.UserFilterResponse;
import api.kitabu.uz.dto.profile.*;
import api.kitabu.uz.entity.AttachEntity;
import api.kitabu.uz.entity.ProfileEntity;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.exeptions.exceptionhandler.APIException;
import api.kitabu.uz.repository.AttachRepository;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.repository.custom.ProfileCustomRepository;
import api.kitabu.uz.telegramBot.dto.ProfileCreateBot;
import api.kitabu.uz.usecases.ProfileUseCase;
import api.kitabu.uz.util.MD5util;
import api.kitabu.uz.util.MapperUtil;
import api.kitabu.uz.util.PageUtil;
import api.kitabu.uz.util.SpringSecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

import static api.kitabu.uz.enums.AppLanguage.en;

@Slf4j
@Service
public class ProfileService implements ProfileUseCase<ProfileRequest, ProfileResponse> {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileRoleService profileRoleService;

    @Autowired
    private AttachRepository attachRepository;
    @Autowired
    private SmsSenderService smsSenderService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private ResourceBundleMessageSource messageSource;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ProfileCustomRepository profileCustomRepository;

    @Override
    public ApiResponse<String> create(ProfileRequest profileRequest, AppLanguage language) {
        Optional<ProfileEntity> byLogin = profileRepository.findByPhoneAndVisible(profileRequest.phone(), true);
        if (byLogin.isPresent()) {
            throw new APIException(messageSource.getMessage("phone.already.exist", null, new Locale(language.name())));
        }
        ProfileEntity entity = new ProfileEntity();
        entity.setName(profileRequest.name());
        entity.setSurname(profileRequest.surname());
        entity.setPhone(profileRequest.phone());
        entity.setPassword(MD5util.encode(profileRequest.password()));
        entity.setStatus(GeneralStatus.ACTIVE);
        if (profileRequest.photoId() != null) {
            Optional<AttachEntity> byId = attachRepository.findById(profileRequest.photoId());
            if (byId.isPresent())
                entity.setPhotoId(profileRequest.photoId());
        }
        profileRepository.save(entity);
        // set Roles after creating profile
        profileRoleService.merge(entity.getId(), profileRequest.roleList());
        return ApiResponse.ok();
    }

    public ApiResponse<String> createForBot(ProfileCreateBot profileRequest) {
        var entity = new ProfileEntity();
        entity.setName(profileRequest.getFirstName());
        entity.setSurname(profileRequest.getLastName());
        entity.setPhone(profileRequest.getPhone());
        entity.setPassword(profileRequest.getPassword());
        entity.setStatus(GeneralStatus.ACTIVE);
        profileRepository.save(entity);
        // set Roles after creating profile
        profileRoleService.merge(entity.getId(), profileRequest.getRoles());
        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<String> delete(String profileId) {
        Optional<ProfileEntity> byId = profileRepository.findByIdAndVisible(profileId, true);
        if (byId.isEmpty())
            throw new APIException("Profile not found");
        Integer delete = profileRepository.delete(false, LocalDateTime.now(), SpringSecurityUtil.getCurrentUserId(), profileId);
        profileRoleService.deleteAllByProfileId(profileId);
        if (delete < 1)
            throw new APIException("Photo not deleted");
        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<ProfileResponseDetail> getProfile(String profileId) {
        return new ApiResponse<>(200, false, profileRepository
                .findByIdAndVisible(profileId, true)
                .map(mapToResponseGet())
                .orElseThrow(() -> new APIException("Profile not found")));
    }

    @Override
    public ApiResponse<String> update(ProfileRequestUpdate profileRequestUpdate, String profileId) {
        Optional<ProfileEntity> byId = profileRepository.findByIdAndVisible(profileId, true);
        if (byId.isEmpty())
            throw new APIException("Profile not found");

        ProfileEntity entity = byId.get();
        if (!profileRequestUpdate.password().equals(entity.getPassword()))
            ProfileRequestUpdate.builder().password(MD5util.encode(profileRequestUpdate.password())).build();


        Integer update = profileRepository.update(profileRequestUpdate.name(), profileRequestUpdate.surname(), profileRequestUpdate.status(), profileRequestUpdate.password(), profileId);
        profileRoleService.merge(profileId, profileRequestUpdate.roleList());
        if (update < 1)
            throw new APIException("not.updated");

        return ApiResponse.ok();
    }

    @Override
    public ApiResponse<Boolean> updateDetail(ProfileDetelUpdate detelUpdate, AppLanguage language) {
        return ApiResponse.ok(profileRepository.updateDeteil(detelUpdate.name(), detelUpdate.surname(), SpringSecurityUtil.getCurrentUserId()) > 0);
    }

    @Override
    public ApiResponse<Boolean> updatePassword(ProfilePasswordUpdate passwordUpdate, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findById(SpringSecurityUtil.getCurrentUserId());
        if (optional.isEmpty()) {
            throw new APIException(messageSource.getMessage("profile.not.found", null, new Locale(language.name())));
        }
        if (!MD5util.encode(passwordUpdate.oldPassword()).equals(optional.get().getPassword())) {
            throw new APIException(messageSource.getMessage("profile.not.found", null, new Locale(language.name())));
        }
        if (!passwordUpdate.newPassword().equals(passwordUpdate.confirmNewPassword())) {
            throw new APIException(
                    messageSource.getMessage("new.password.is.not.same.with.repeated.new.password",
                            null, new Locale(language.name())));
        }
        return ApiResponse.ok(profileRepository.updatePassword(MD5util.encode(passwordUpdate.newPassword()), MD5util.encode(passwordUpdate.oldPassword()), SpringSecurityUtil.getCurrentUserId()) == 1);
    }

    @Override
    public ApiResponse<String> updatePhoto(String photoId) {
        String profileId = SpringSecurityUtil.getCurrentUserId();
        var entity = getById(profileId, en);
        profileRepository.updatePhoto(photoId, profileId);
        if (!(entity.getPhotoId() == null)) {
            attachService.delete(entity.getPhotoId(), en);
        }
        return ApiResponse.ok();
    }

    public ProfileEntity getById(String id, AppLanguage language) {
        return profileRepository
                .findByIdAndVisible(id, true)
                .orElseThrow(() -> new APIException(messageSource.getMessage("profile.not.found", null, new Locale(language.name()))));
    }

    public ProfileResponse.ProfileResponseShort getByIdComment(String id) {
        var profile = profileRepository
                .findByIdAndVisible(id, true).get();
        return new ProfileResponse.ProfileResponseShort(
                profile.getId(),
                profile.getName(),
                profile.getSurname(),
                profile.getPhone(),
                attachService.asUrlString(profile.getPhotoId())
        );
    }

    @Override
    public ApiResponse<Page<ProfileResponse>> getAll(int page, int size) {
        PageRequest paging = PageRequest.of(PageUtil.getPage(page), size);
        Page<Object[]> response = profileRepository.getAllPagination(paging);
        List<ProfileResponse> profileResponseList = new ArrayList<>();
        for (Object[] objects : response) {
            var dto = ProfileResponse
                    .builder()
                    .id(MapperUtil.getStringValue(objects[0]))
                    .name(MapperUtil.getStringValue(objects[1]))
                    .surname(MapperUtil.getStringValue(objects[2]))
                    .phone(MapperUtil.getStringValue(objects[3]))
                    .password(MapperUtil.getStringValue(objects[4]))
                    .photoId(attachService.asUrlString(MapperUtil.getStringValue(objects[5])))
                    .roleList(profileRoleService.getByProfileId(MapperUtil.getStringValue(objects[0])))
                    .status(GeneralStatus.valueOf(MapperUtil.getStringValue(objects[6])))
                    .createdDate(MapperUtil.getLocalDateValue(objects[7]))
                    .build();
            profileResponseList.add(dto);
        }
        return new ApiResponse<>(200, false, new PageImpl<>(profileResponseList, paging, response.getTotalElements()));
    }

    public ApiResponse<?> updatePhone(ProfilePhoneUpdate dto, AppLanguage language) {
        Optional<ProfileEntity> optional = profileRepository.findByPhoneAndVisible(dto.newPhone(), Boolean.TRUE);
        if (optional.isPresent()) {
            log.info("{} Phone exist", dto.newPhone());
            return ApiResponse.bad(messageSource.getMessage("phone.already.exist", null, new Locale(language.name())));
        }
        // send new phone sms code
        smsSenderService.sendChangePhoneSms(dto.newPhone(), dto.signature());
        // save temp phone
        profileRepository.changeNewPhone(SpringSecurityUtil.getCurrentUserId(), dto.newPhone());
        return ApiResponse.ok(messageSource.getMessage("verification.code.sent", null, new Locale(language.name())));
    }

    public ApiResponse<?> updatePhoneVerification(String code, AppLanguage language) {
        ProfileEntity profile = SpringSecurityUtil.getCurrentEntity();
        // check sms code
        smsHistoryService.checkSmsCode(profile.getTempPhone(), code, language);
        // change phone
        int result = profileRepository.changePhone(profile.getId(), profile.getTempPhone());
        return ApiResponse.ok();
    }

    private Function<ProfileEntity, ProfileResponse> mapToResponse() {
        return profileEntity -> new ProfileResponse(
                profileEntity.getId(),
                profileEntity.getName(),
                profileEntity.getSurname(),
                profileEntity.getPhone(),
                profileEntity.getPassword(),
                profileEntity.getPhotoId(),
                profileEntity.getStatus(),
                profileRoleService.getByProfileId(profileEntity.getId()),
                profileEntity.getCreatedDate());
    }

    private Function<ProfileEntity, ProfileResponseDetail> mapToResponseGet() {
        return profileEntity -> new ProfileResponseDetail(
                profileEntity.getName(),
                profileEntity.getSurname(),
                profileEntity.getPhone(),
                profileEntity.getPhotoId(),
                profileRoleService.getByProfileId(profileEntity.getId()));
    }

    @Override
    public ApiResponse<PageImpl<UserFilterResponse>> filter(UserFilterRequest filterRequest,
                                                            int page, int size) {
        PageRequest paging = PageRequest.of(PageUtil.getPage(page), size);
        var filterResponse = profileCustomRepository.filter(filterRequest, PageUtil.getPage(page), size);
        return new ApiResponse<>(200, false,
                new PageImpl<>(filterResponse.getList(), paging, filterResponse.getTotalCount()));
    }

    @Override
    public ApiResponse<List<?>> roleChange(String userId, RoleChangeRequest dto, AppLanguage language) {
        var responseUser = getById(userId, language);
        profileRoleService.merge(responseUser.getId(),dto.roles());
        return ApiResponse.ok(dto.roles().stream().toList());
    }


    public ApiResponse<Boolean> deleteCurrentProfile() {
        String currentUserId = SpringSecurityUtil.getCurrentUserId();
        int i = profileRepository.currentProfileDelete(currentUserId);
        return ApiResponse.ok(i == 1);
    }
}
