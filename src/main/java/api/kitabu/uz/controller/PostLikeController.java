package api.kitabu.uz.controller;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.dto.post.PostLikeRequest;
import api.kitabu.uz.dto.post.PostResponse;
import api.kitabu.uz.enums.AppLanguage;
import api.kitabu.uz.usecases.PostLikeUseCase;
import api.kitabu.uz.util.SpringSecurityUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/post_like")
@Slf4j
@Tag(name = "PostLike Api list")
public class PostLikeController {
    @Autowired
    private PostLikeUseCase<PostLikeRequest> postLikeUseCase;

    @PostMapping({"/like_system"})
    @PreAuthorize("hasRole('ROLE_USER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Create like", description = "")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ApiResponse<PostResponse.PostLikeAndDislike>> like(@Valid @RequestBody PostLikeRequest request,
                                                          @RequestHeader(value = "Accept-language", defaultValue = "uz") AppLanguage language) {
        log.info("Like and Dislike: {}", request);
        return ResponseEntity.ok(postLikeUseCase.create(request, language, SpringSecurityUtil.getCurrentUserId()));
    }
}
