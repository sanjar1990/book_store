package api.kitabu.uz.dto.post;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostGenreDTO {
    private String id;
    private String postId;
    private String genreId;
    private LocalDateTime createdDate;
}
