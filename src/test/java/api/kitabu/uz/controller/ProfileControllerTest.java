package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.profile.ProfileRequest;
import api.kitabu.uz.dto.profile.ProfileRequestUpdate;
import api.kitabu.uz.dto.profile.ProfileResponseDetail;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.ProfileRole;
import api.kitabu.uz.usecases.ProfileUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ProfileControllerTest {

    @MockBean
    private CustomDetailService customDetailService;
    private MockMvc mockMvc;

    @Mock
    private ProfileUseCase<ProfileRequest, ?> profileUseCase;

    @InjectMocks
    private ProfileController profileController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(profileController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testCreateProfile() throws Exception {
        ProfileRequest request = new ProfileRequest("John", "Doe", "1234567890", "john@example.com","sdsd",List.of(ProfileRole.ROLE_ADMIN));
        when(profileUseCase.create(any(ProfileRequest.class), any(AppLanguage.class)))
                .thenReturn(ApiResponse.ok("profileId123"));

        mockMvc.perform(post("/api/v1/profile/create")
                        .header("Accept-Language", "uz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("profileId123"));
    }

    @Test
    void testGetProfileById() throws Exception {
        ProfileResponseDetail responseDetail = new ProfileResponseDetail("profileId123", "John", "Doe", "1234567890", List.of(ProfileRole.ROLE_ADMIN));
        when(profileUseCase.getProfile("profileId123")).thenReturn(ApiResponse.ok(responseDetail));

        mockMvc.perform(get("/api/v1/profile/get/by/profileId123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("profileId123"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.phone").value("1234567890"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test
    void testUpdateProfile() throws Exception {
        ProfileRequestUpdate updateRequest = new ProfileRequestUpdate("JohnUpdated", "DoeUpdated", "1234567890", GeneralStatus.NOT_ACTIVE,List.of(ProfileRole.ROLE_ADMIN));
        when(profileUseCase.update(any(ProfileRequestUpdate.class), any(String.class))).thenReturn(ApiResponse.ok("profileId123"));

        mockMvc.perform(put("/api/v1/profile/update/profileId123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("profileId123"));
    }


}
