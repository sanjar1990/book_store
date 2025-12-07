package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.comment.PostCommentRequest;
import api.kitabu.uz.dto.comment.PostCommentResponse;
import api.kitabu.uz.dto.filter.comment.CommentFilterRequest;
import api.kitabu.uz.dto.filter.comment.CommentFilterResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.Lang;
import api.kitabu.uz.usecases.CommentUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security for unit testing
class CommentControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentUseCase<PostCommentRequest, PostCommentResponse> commentUseCase;

    @Test
    void testCreateComment() throws Exception {
        PostCommentRequest request = new PostCommentRequest("post123", "This is comment");
        PostCommentResponse response = PostCommentResponse.builder()
                .id("1")
                .postId("post123")
                .content("This is comment")
                .build();

        Mockito.when(commentUseCase.create(any(), eq(AppLanguage.uz)))
                .thenReturn((ApiResponse) ApiResponse.ok(response));

        mockMvc.perform(post("/api/v1/comment")
                        .header("Accept-language", "uz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"))
                .andExpect(jsonPath("$.data.content").value("This is comment"));
    }

    @Test
    void testGetCommentById() throws Exception {
        PostCommentResponse response = PostCommentResponse.builder()
                .id("1")
                .postId("post123")
                .content("This is comment")
                .build();        Mockito.when(commentUseCase.getById(eq("1"), eq(AppLanguage.uz)))
                .thenReturn((ApiResponse) ApiResponse.ok(response));

        mockMvc.perform(get("/api/v1/comment/1")
                        .header("Accept-language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value("1"));
    }

    @Test
    void testDeleteComment() throws Exception {
        Mockito.when(commentUseCase.delete(eq("1"), eq(AppLanguage.uz)))
                .thenReturn((ApiResponse) ApiResponse.ok(true));

        mockMvc.perform(delete("/api/v1/comment/1")
                        .header("Accept-language", "uz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testPagination() throws Exception {
        PostCommentResponse comment1 = PostCommentResponse.builder()
                .id("1")
                .postId("post123")
                .content("comment1")
                .build();
        PostCommentResponse comment2 = PostCommentResponse.builder()
                .id("2")
                .postId("post123")
                .content("Tcomment2")
                .build();


        PageImpl<PostCommentResponse> page = new PageImpl<>(List.of(comment1, comment2));
        Mockito.when(commentUseCase.pageable(eq("post123"), eq(1), eq(10)))
                .thenReturn((ApiResponse) ApiResponse.ok(page));

        mockMvc.perform(get("/api/v1/comment/pagination/post123")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value("1"))
                .andExpect(jsonPath("$.data.content[1].id").value("2"));
    }

    @Test
    void testPaginationByProfileId() throws Exception {
        PostCommentResponse comment1 = PostCommentResponse.builder()
                .id("1")
                .postId("post123")
                .content("comment1")
                .build();
        PageImpl<PostCommentResponse> page = new PageImpl<>(List.of(comment1));
        Mockito.when(commentUseCase.pageableByProfileId(eq(1), eq(10)))
                .thenReturn((ApiResponse) ApiResponse.ok(page));

        mockMvc.perform(get("/api/v1/comment/paginationByProfileId")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].id").value("1"));
    }

    @Test
    void testIsRead() throws Exception {
        Mockito.when(commentUseCase.isRead(eq("1"), eq(true)))
                .thenReturn((ApiResponse) ApiResponse.ok(true));

        mockMvc.perform(put("/api/v1/comment/isReadByCommentId/1")
                        .param("isRead", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    void testFilterAsAdmin() throws Exception {
        CommentFilterRequest filterRequest = new CommentFilterRequest("1", "1", "1");
        CommentFilterResponse filterResponse = new CommentFilterResponse("1", "1", "1", LocalDateTime.now());

        PageImpl<CommentFilterResponse> page = new PageImpl<>(List.of(filterResponse));

        Mockito.when(commentUseCase.filterAsAdmin(eq(filterRequest), eq(Lang.en), eq(1), eq(10)))
                .thenReturn((ApiResponse) ApiResponse.ok(page));

        mockMvc.perform(post("/api/v1/comment/filter_admin")
                        .header("Accept-Language", "en")
                        .param("page", "1")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(filterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0]").exists());
    }
}
