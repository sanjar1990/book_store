package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(ReportController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
class ReportControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ReportService reportService;

    @Test void testCreateReport() throws Exception { // Arrange ReportRequest request = new ReportRequest("postId123", "Inappropriate content"); ReportResponse response = new ReportResponse("reportId123", "postId123", "Inappropriate content"); when(reportService.create(any(ReportRequest.class), any(AppLanguage.class))) .thenReturn(response); // Act & Assert mockMvc.perform(post("/api/v1/report/create") .header("Accept-Language", "uz") .contentType(MediaType.APPLICATION_JSON) .content(objectMapper.writeValueAsString(request))) .andExpect(status().isOk()) .andExpect(jsonPath("$.data.title").value("reportId123")) .andExpect(jsonPath("$.data.postId").value("postId123")) .andExpect(jsonPath("$.data.profileId").value("Inappropriate content")); }
}
}
