package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.filter.user.UserFilterRequest;
import api.kitabu.uz.dto.filter.user.UserFilterResponse;
import api.kitabu.uz.dto.profile.*;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.mappers.ProfileMapper;
import api.kitabu.uz.usecases.ProfileUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile Api list")
public class ProfileController {
    @Autowired
    private ProfileUseCase<ProfileRequest, ProfileResponse> profileUseCase;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create profile", description = "")
    public ResponseEntity<ApiResponse<String>> create(@RequestBody ProfileRequest profileRequest,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Profile create: {}", profileRequest.phone());
        return ResponseEntity.ok(profileUseCase.create(profileRequest, language));
    }

    @PutMapping("/update/{profileId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update Profile by Id", description = "")
    public ResponseEntity<ApiResponse<String>> update(@RequestBody ProfileRequestUpdate profileRequestUpdate, @PathVariable String profileId) {
        log.info("Profile update by id: {}", profileId);
        return ResponseEntity.ok(profileUseCase.update(profileRequestUpdate, profileId));
    }

    @PutMapping("/update/photo/{photoId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Update profile photo", description = "")
    public ResponseEntity<ApiResponse<String>> updatePhoto(@PathVariable String photoId) {
        log.info("Update profile photo", photoId);
        return ResponseEntity.ok(profileUseCase.updatePhoto(photoId));

    }

    @PutMapping(value = "/detail")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update ProfileDetail", description = "")
    public ResponseEntity<ApiResponse<Boolean>> updateDetail(@RequestBody ProfileDetelUpdate detelUpdate,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        return ResponseEntity.ok(profileUseCase.updateDetail(detelUpdate, language));
    }

    @PutMapping("/updatePassword")
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Update ProfilePassword", description = "")
    public ResponseEntity<ApiResponse<Boolean>> updatePassword(@RequestBody ProfilePasswordUpdate passwordUpdate,
                                                               @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        return ResponseEntity.ok(profileUseCase.updatePassword(passwordUpdate, language));
    }

    @GetMapping("/get/by/{profileId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Profile get By id", description = "")
    public ResponseEntity<ApiResponse<ProfileResponseDetail>> getById(@PathVariable String profileId) {
        log.info("Get profileRole by id: {}", profileId);
        return ResponseEntity.ok(profileUseCase.getProfile(profileId));
    }

    @GetMapping("/get/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Get All profile", description = "")
    public ResponseEntity<ApiResponse<Page<ProfileResponse>>> getAll(@RequestParam(value = "page", defaultValue = "1") int page,
                                                                     @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Get profile list: {}"); // I have to ask this part, why we use .size()
        return ResponseEntity.ok(profileUseCase.getAll(page, size));
    }

    @DeleteMapping("/delete/by/{profileId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Delete Profile by Id", description = "")
    public ResponseEntity<ApiResponse<String>> deleteById(@PathVariable String profileId) {
        log.info("Delete profile by id: {}", profileId);
        return ResponseEntity.ok(profileUseCase.delete(profileId));
    }


    /**
     * Update Phone
     */

    @PutMapping("/update-phone")
    @Operation(summary = "Update  profile phone  api", description = "for user")
    public ResponseEntity<ApiResponse<?>> updatePhone(@RequestBody ProfilePhoneUpdate dto,
                                                      @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Update phone {}", dto.newPhone());
        return ResponseEntity.ok(profileUseCase.updatePhone(dto, language));
    }


    @PutMapping("/phone-verification/{code}")
    @Operation(summary = "Update  profile phone verification api", description = "for user")
    public ResponseEntity<ApiResponse<?>> updatePhoneVerification(@PathVariable("code") String code,
                                                                  @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Update phone verification {}", code);
        return ResponseEntity.ok(profileUseCase.updatePhoneVerification(code, language));
    }

    @PutMapping("/filter")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin for users sort", description = "for admin")
    public ResponseEntity<ApiResponse<PageImpl<UserFilterResponse>>> filter(@RequestBody UserFilterRequest filterRequest,
                                                                            @RequestParam(value = "page", defaultValue = "1") int page,
                                                                            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Filter request: {}", filterRequest);
        return ResponseEntity.ok(profileUseCase.filter(filterRequest, page, size));
    }

    @PutMapping("/change_role/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")

    @Operation(summary = "change role", description = "for admin")
    public ResponseEntity<ApiResponse<?>> changeRole(@PathVariable("id") String userId,
                                                     @RequestBody RoleChangeRequest dto,
                                                     @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        return ResponseEntity.ok(profileUseCase.roleChange(userId, dto, language));
    }


    @GetMapping(path = "/delete-current-profile")
    @Operation(summary = "I`m delete")
    public ResponseEntity<ApiResponse<Boolean>> deleteCurrentProfile() {
        log.info("I am delete:");
        return ResponseEntity.ok(profileUseCase.deleteCurrentProfile());
    }

}
