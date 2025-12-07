package api.kitabu.uz.dto.post;

import api.kitabu.uz.enums.PostLikeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostLikeRequest {
    @NotBlank @NotNull
    private String postId;
    private PostLikeStatus status;
}
