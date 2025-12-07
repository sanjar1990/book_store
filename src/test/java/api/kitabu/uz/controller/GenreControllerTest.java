package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.genre.GenreRequest;
import api.kitabu.uz.dto.genre.GenreResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.mappers.GenreLangMapper;
import api.kitabu.uz.usecases.GenreUseCase;
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

@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false) // disables security for testing
class GenreControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private GenreUseCase<GenreRequest, GenreResponse> genreUseCase;

    @Test
    void testCreateGenre() throws Exception {
        GenreRequest request = new GenreRequest("uzTitle", "ruTitle", "enTitle", 1);
        GenreResponse response = new GenreResponse("1", "uzTitle", "ruTitle", "enTitle", 1, LocalDateTime.now());
        Mockito.when(genreUseCase.create(any(), eq(AppLanguage.uz))).thenReturn(ApiResponse.ok(response));

        mockMvc.perform(post("/api/v1/genre/create")
                        .header("Accept-Language", "uz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.titleUz").value("uzTitle"));
    }

    @Test
    void testUpdateGenre() throws Exception {
        GenreRequest request = new GenreRequest("updatedUz", "updatedRu", "updatedEn",1);
        GenreResponse response = new GenreResponse("1", "updatedUz", "updatedRu", "updatedEn", 1, LocalDateTime.now());
        Mockito.when(genreUseCase.update(any(), eq("1"), eq(AppLanguage.uz))).thenReturn(ApiResponse.ok(response));

        mockMvc.perform(put("/api/v1/genre/update/1")
                        .header("Accept-Language", "uz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.titleUz").value("updatedUz"));
    }

    @Test
    void testDeleteGenre() throws Exception {
        GenreResponse.GenreResponseFull response = new GenreResponse.GenreResponseFull("1",
                "uz", "ru", "en", 1, LocalDateTime.now(), LocalDateTime.now(),"ssss",true);
        Mockito.when(genreUseCase.delete(eq("1"), eq(AppLanguage.uz))).thenReturn(ApiResponse.ok(response));

        mockMvc.perform(delete("/api/v1/genre/delete/by/1")
                        .header("Accept-Language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void testGetGenreById() throws Exception {
        GenreResponse response = new GenreResponse("1", "uzTitle", "ruTitle", "enTitle", 1, LocalDateTime.now());
        Mockito.when(genreUseCase.getGenre(eq("1"), eq(AppLanguage.uz))).thenReturn(ApiResponse.ok(response));

        mockMvc.perform(get("/api/v1/genre/get/by/1")
                        .header("Accept-Language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.titleUz").value("uzTitle"));
    }



    @Test
    void testGetAllGenres() throws Exception {
        List<GenreResponse> list = List.of(new GenreResponse("1", "uzTitle", "ruTitle", "enTitle", 1, LocalDateTime.now()));
        Mockito.when(genreUseCase.getAll()).thenReturn(ApiResponse.ok(list));

        mockMvc.perform(get("/api/v1/genre/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value("1"));
    }
}
