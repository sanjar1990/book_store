package api.kitabu.uz.dto.filter.comment;

public record CommentFilterRequest(
                String profileId,
                String commentId,
                String postId
        ) {
}
