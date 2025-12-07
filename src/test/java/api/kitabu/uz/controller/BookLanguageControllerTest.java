package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.KeyValueDTO;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.service.BookLanguageService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookLanguageController.class)
@AutoConfigureMockMvc(addFilters = false)
class BookLanguageControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookLanguageService bookLanguageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBookLanguage() throws Exception {

        KeyValueDTO kv1 = new KeyValueDTO();
        kv1.setKey("en");
        kv1.setValue("English");

        KeyValueDTO kv2 = new KeyValueDTO();
        kv2.setKey("ru");
        kv2.setValue("Russian");

        List<KeyValueDTO> languages = List.of(kv1, kv2);

        Mockito.when(bookLanguageService.getByLanguage(eq(AppLanguage.uz)))
                .thenReturn(languages);

        mockMvc.perform(
                        get("/api/v1/book-language")
                                .header("Accept-Language", "uz")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].key").value("en"))
                .andExpect(jsonPath("$.data[0].value").value("English"))
                .andExpect(jsonPath("$.data[1].key").value("ru"))
                .andExpect(jsonPath("$.data[1].value").value("Russian"));
    }
}
