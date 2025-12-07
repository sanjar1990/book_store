package api.kitabu.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ProfileDetelUpdate(
        @NotBlank
        String name,
        @NotBlank
        String surname
) {
}
