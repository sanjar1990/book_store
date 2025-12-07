package api.kitabu.uz.controller;

import api.kitabu.uz.config.CustomDetailService;
import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.post.PostLikeRequest;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.enums.PostLikeStatus;
import api.kitabu.uz.usecases.PostLikeUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostLikeController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security for testing
class PostLikeControllerTest {
    @MockBean
    private CustomDetailService customDetailService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostLikeUseCase<PostLikeRequest> postLikeUseCase;

    @Test
    void testLikePost() throws Exception {
        PostLikeRequest request = new PostLikeRequest("postId123", PostLikeStatus.LIKE);
        PostResponse.PostLikeAndDislike response = new PostResponse.PostLikeAndDislike(10, 2);

        // Mocking the service
        Mockito.when(postLikeUseCase.create(any(), eq(AppLanguage.uz), eq("currentUserId")))
                .thenReturn(ApiResponse.ok(response));

        // Perform the POST request
        mockMvc.perform(post("/api/v1/post_like/like_system")
                        .header("Accept-Language", "uz")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(10))
                .andExpect(jsonPath("$.data.dislikeCount").value(2));
    }
}
