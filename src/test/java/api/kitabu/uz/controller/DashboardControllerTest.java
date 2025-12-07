package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.service.DashboardService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false) // disables Spring Security for testing
class DashboardControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private DashboardService dashboardService;

    @Test
    void testGetAdminDashboardData() throws Exception {
        // Mock the service response
        ApiResponse<String> mockedResponse = ApiResponse.ok("Dashboard counts data");
        when(dashboardService.getAdminDashboardData()).thenReturn(mockedResponse);

        mockMvc.perform(get("/api/v1/dashboard")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value("Dashboard counts data"));
    }
}
