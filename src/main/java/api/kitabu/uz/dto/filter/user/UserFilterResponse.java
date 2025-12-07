package api.kitabu.uz.dto.filter.user;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record UserFilterResponse(
        String id,
        String imageUrl,
        String name,
        String surname,
        String phone,
        String status,
        LocalDateTime createdDate
) {
}
