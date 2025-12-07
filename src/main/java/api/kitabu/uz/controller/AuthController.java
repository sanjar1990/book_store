package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.auth.*;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.repository.ProfileRepository;
import api.kitabu.uz.service.AuthService;
import api.kitabu.uz.util.SpringSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Api list", description = "")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    @Operation(summary = "User login", description = "")
    public ResponseEntity<ApiResponse<AuthorizationResponseDTO>> login(@RequestBody AuthorizationDTO dto,
                                                                       @RequestHeader(value = "Accept-Language", required = false, defaultValue = "uz") Lang lang) {
        log.info("Registration auth: {} ", dto.getPhone());
        return ResponseEntity.ok(authService.authorization(dto, lang));
    }

    /**
     * Registration
     */
    @PostMapping("/registration")
    public ResponseEntity<ApiResponse<?>> registrationUser(@Valid @RequestBody RegistrationUserDTO dto,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage lang) {
        return ResponseEntity.ok(authService.registration(dto, lang));
    }


    @Operation(summary = "User registration verification", description = "Method used for user registration verification")
    @PostMapping("/registration/verification")
    public ResponseEntity<ApiResponse<AuthorizationResponseDTO>> registrationVerification(@RequestBody @Valid SmsVerificationDTO dto,
                                                                                          @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Registration verification phone: {}", dto.getPhone());
        return ResponseEntity.ok(authService.registrationVerification(dto, language));
    }

    /**
     * Rest password
     */
    @PostMapping("/reset-password")
    @Operation(summary = "Profile reset password", description = "Method profile for  reset password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody @Valid AuthResetProfileDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Profile reset password {}", dto);
        return ResponseEntity.ok(authService.resetPasswordRequest(dto, language));
    }

    @PutMapping("/reset-password/confirm")
    @Operation(summary = "Profile reset password confirm", description = "Method profile for  reset password confirm")
    public ResponseEntity<ApiResponse<AuthorizationResponseDTO>> resetPasswordConfirm(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                                                      @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("Profile Reset password confirm {}", dto);
        return ResponseEntity.ok(authService.resetPasswordConfirm(dto, language));

    }

    @PutMapping("/resend/sms/{phoneNumber}")
    @Operation(summary = "", description = "")
    public ResponseEntity<ApiResponse<?>> resend(@PathVariable String phoneNumber,
                                                 @RequestHeader(value = "Accept-Language", defaultValue = "uz") AppLanguage language) {
        log.info("resend sms code", phoneNumber);
        return ResponseEntity.ok(authService.resendSms(phoneNumber, language));

    }

}
