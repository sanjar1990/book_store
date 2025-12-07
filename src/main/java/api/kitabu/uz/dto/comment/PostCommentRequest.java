package api.kitabu.uz.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record PostCommentRequest(
        @NotBlank(message = "Content is empty")
        String content,
        String postId
) {}
