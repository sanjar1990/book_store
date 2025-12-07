package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.region.RegionLangResponse;
import api.kitabu.uz.dto.region.RegionRequest;
import api.kitabu.uz.dto.region.RegionResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.usecases.RegionUserCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RegionController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
class RegionControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RegionUserCase<RegionRequest, RegionResponse> regionUserCase;

    @Test
    void testCreateRegion() throws Exception {
        RegionRequest request = new RegionRequest("Toshkent", "Toshkent", "Toshkent");
        RegionResponse response = new RegionResponse(1, "Toshkent", "Toshkent", "Toshkent", true, LocalDateTime.now());

        Mockito.when(regionUserCase.createRegion(any())).thenReturn(new ApiResponse<>(200, false, response));

        mockMvc.perform(post("/api/v1/region/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nameUz").value("Toshkent"));
    }

    @Test
    void testDeleteRegion() throws Exception {
        Mockito.when(regionUserCase.deleteRegion(eq(1), eq(AppLanguage.uz)))
                .thenReturn(new ApiResponse<>(200, false, true));

        mockMvc.perform(delete("/api/v1/region/1")
                        .header("Accept-language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testUpdateRegion() throws Exception {
        RegionRequest request = new RegionRequest("Toshkent", "Toshkent", "Toshkent");
        RegionResponse response = new RegionResponse(1, "Toshkent", "Toshkent", "Toshkent", true, LocalDateTime.now());


        Mockito.when(regionUserCase.updateRegion(any(), eq(2), eq(AppLanguage.uz)))
                .thenReturn(new ApiResponse<>(200, false, response));

        mockMvc.perform(put("/api/v1/region/2")
                        .header("Accept-language", "uz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(2))
                .andExpect(jsonPath("$.data.nameUz").value("Samarqand"));
    }

    @Test
    void testGetAllRegions() throws Exception {
        RegionResponse r1 = new RegionResponse(1, "Toshkent", "Toshkent", "Toshkent", true, LocalDateTime.now());
        RegionResponse r2 = new RegionResponse(2, "Samarqand", "Samarqand", "Samarqand", true, LocalDateTime.now());

        Mockito.when(regionUserCase.getAllRegion()).thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/region"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nameUz").value("Toshkent"))
                .andExpect(jsonPath("$.data[1].nameUz").value("Samarqand"));
    }

    @Test
    void testGetRegionById() throws Exception {
        RegionResponse response = new RegionResponse(1, "Toshkent", "Toshkent", "Toshkent", true, LocalDateTime.now());

        Mockito.when(regionUserCase.getRegionById(eq(1), eq(AppLanguage.uz)))
                .thenReturn(new ApiResponse<>(200, false, response));

        mockMvc.perform(get("/api/v1/region/1")
                        .header("Accept-language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.nameUz").value("Toshkent"));
    }

    @Test
    void testGetAllByLang() throws Exception {
        RegionLangResponse r1 = new RegionLangResponse(1, "Toshkent");
        RegionLangResponse r2 = new RegionLangResponse(2, "Samarqand");

        Mockito.when(regionUserCase.getShortInfo(eq(AppLanguage.uz)))
                .thenReturn(List.of(r1, r2));

        mockMvc.perform(get("/api/v1/region/by-lang")
                        .header("Accept-language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].nameUz").value("Toshkent"))
                .andExpect(jsonPath("$.data[1].nameUz").value("Samarqand"));
    }
}
