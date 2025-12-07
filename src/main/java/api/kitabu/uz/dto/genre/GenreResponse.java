package api.kitabu.uz.dto.genre;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record GenreResponse(
        String id,
        String titleUz,
        String titleEn,
        String titleRu,
        Integer orderNumber,
        LocalDateTime createdDate
) {
    @Builder
    public record GenreResponseGetAll(
            String id,
            String name,
            Integer orderNumber,
            LocalDateTime createdDate
    ) {
    }
   @Builder
    public record GenreResponseShort(
            String title,
            Integer orderNumber
    ) {
    }
    @Builder
    public record GenreResponseFull(
            String id,
            String titleUz,
            String titleEn,
            String titleRu,
            Integer orderNumber,
            LocalDateTime createdDate,
            LocalDateTime deletedDate,
            String deletedId,
            Boolean visible
    ) {
    }

}
