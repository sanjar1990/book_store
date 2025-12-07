package api.kitabu.uz.dto.region;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record RegionRequest(
        @NotBlank @NotNull
        String nameUz,
        @NotBlank @NotNull
        String nameRu,
        @NotBlank @NotNull
        String nameEn
) {
}
