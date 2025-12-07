package api.kitabu.uz.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FileResponse(
        String id,
        String filename,
        String path,
        Long size,
        String url,
        String extension,
        LocalDateTime createdDate) {
}
