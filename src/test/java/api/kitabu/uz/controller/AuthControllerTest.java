package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.auth.*;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;



    @Test
    void testLogin() throws Exception {
        AuthorizationDTO dto = new AuthorizationDTO();
        dto.setPhone("998901112233");
        dto.setPassword("12345");

        AuthorizationResponseDTO responseDTO = new AuthorizationResponseDTO();
        responseDTO.setToken("jwt-token");

        ApiResponse<AuthorizationResponseDTO> response =
                ApiResponse.ok(responseDTO);

        Mockito.when(authService.authorization(any(), eq(Lang.uz)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "uz")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }


    @Test
    void testRegistration() throws Exception {
        RegistrationUserDTO dto = new RegistrationUserDTO();
        dto.setPhone("998901234567");
        dto.setPassword("abcd1234");

        ApiResponse<Object> response = ApiResponse.ok("registered");
        Mockito.when(authService.registration(any(), eq(AppLanguage.uz)))
                .thenReturn((ApiResponse) ApiResponse.ok("registered"));
        mockMvc.perform(
                        post("/api/v1/auth/registration")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "uz")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("registered"));
    }



    @Test
    void testRegistrationVerification() throws Exception {
        SmsVerificationDTO dto = new SmsVerificationDTO();
        dto.setPhone("998901112233");
        dto.setCode("1234");

        AuthorizationResponseDTO authRes = new AuthorizationResponseDTO();
        authRes.setToken("jwt-token");

        Mockito.when(authService.registrationVerification(any(), eq(AppLanguage.uz)))
                .thenReturn(ApiResponse.ok(authRes));

        mockMvc.perform(
                        post("/api/v1/auth/registration/verification")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "uz")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }



    @Test
    void testResetPassword() throws Exception {
        AuthResetProfileDTO dto = new AuthResetProfileDTO();
        dto.setPhone("998901234567");

        Mockito.when(authService.resetPasswordRequest(any(), eq(AppLanguage.uz)))
                .thenReturn(ApiResponse.ok("SMS sent"));

        mockMvc.perform(
                        post("/api/v1/auth/reset-password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "uz")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("SMS sent"));
    }



    @Test
    void testResetPasswordConfirm() throws Exception {
        ResetPasswordConfirmDTO dto = new ResetPasswordConfirmDTO();
        dto.setPhone("998901234567");
        dto.setCode("1111");
        dto.setNewPassword("newPass");

        AuthorizationResponseDTO authRes = new AuthorizationResponseDTO();
        authRes.setToken("new-token");

        Mockito.when(authService.resetPasswordConfirm(any(), eq(AppLanguage.uz)))
                .thenReturn(ApiResponse.ok(authRes));

        mockMvc.perform(
                        put("/api/v1/auth/reset-password/confirm")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Accept-Language", "uz")
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("new-token"));
    }



    @Test
    void testResendSms() throws Exception {
        Mockito.when(authService.resendSms(eq("998900000000"), eq(AppLanguage.uz)))
                .thenReturn((ApiResponse)ApiResponse.ok("resent"));

        mockMvc.perform(
                        put("/api/v1/auth/resend/sms/998900000000")
                                .header("Accept-Language", "uz")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("resent"));
    }
}
