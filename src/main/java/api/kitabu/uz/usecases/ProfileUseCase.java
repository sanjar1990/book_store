package api.kitabu.uz.usecases;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.user.UserFilterRequest;
import api.kitabu.uz.dto.filter.user.UserFilterResponse;
import api.kitabu.uz.dto.profile.*;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.ProfileMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public interface ProfileUseCase<REQUEST, RESPONSE> {
    ApiResponse<String> create(REQUEST profileRequest,AppLanguage language);

    ApiResponse<String> delete(String profileId);

    ApiResponse<ProfileResponseDetail> getProfile(String profileId);

    ApiResponse<String> update(ProfileRequestUpdate profileRequestUpdate, String profileId);

    ApiResponse<String> updatePhoto(String photoId);

    ApiResponse<Page<ProfileResponse>> getAll(int page, int size);

    ApiResponse<?> updatePhone(ProfilePhoneUpdate dto, AppLanguage language);

    ApiResponse<?> updatePhoneVerification(String smsCode,AppLanguage language);
    ApiResponse<Boolean>updateDetail(ProfileDetelUpdate detelUpdate, AppLanguage language);

    ApiResponse<Boolean> updatePassword(ProfilePasswordUpdate passwordUpdate,AppLanguage language);

    ApiResponse<PageImpl<UserFilterResponse>> filter(UserFilterRequest filterRequest, int page, int size);

    ApiResponse<?> roleChange(String userId, RoleChangeRequest dto, AppLanguage language);

    ApiResponse<Boolean> deleteCurrentProfile();
}
