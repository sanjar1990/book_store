package api.kitabu.uz.dto.filter.comment;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record CommentFilterResponse(
         String content,
         String postId,
         String  profileId,
         LocalDateTime createdDate
) {
}
