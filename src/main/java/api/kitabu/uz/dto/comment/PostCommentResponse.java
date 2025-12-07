package api.kitabu.uz.dto.comment;

import api.kitabu.uz.dto.profile.ProfileResponse;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record PostCommentResponse(
        String id,
        String content,
        ProfileResponse.ProfileResponseShort profile,
        String postId,
        String postTitle,
        LocalDateTime createdDate,
        Boolean visible,
        Boolean isRead
        ) {
}
