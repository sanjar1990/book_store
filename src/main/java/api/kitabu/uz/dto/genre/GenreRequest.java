package api.kitabu.uz.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record GenreRequest(
        @NotBlank
        String titleUz,
        @NotBlank
        String titleEn,
        @NotBlank
        String titleRu,
        @NotBlank
        Integer orderNumber
) {

}
