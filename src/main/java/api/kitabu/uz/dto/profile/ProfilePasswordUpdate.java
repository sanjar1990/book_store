package api.kitabu.uz.dto.profile;

import lombok.Builder;

@Builder
public record ProfilePasswordUpdate(
        String oldPassword,
        String newPassword,
        String confirmNewPassword
) {
}
