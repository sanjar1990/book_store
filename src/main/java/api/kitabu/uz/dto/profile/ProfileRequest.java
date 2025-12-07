package api.kitabu.uz.dto.profile;

import api.kitabu.uz.enums.ProfileRole;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record ProfileRequest(
        @NotBlank
        String name,
        @NotBlank
        String surname,
        @NotBlank
        String phone,
        @NotBlank
        String password,
        String photoId,
        @NotBlank
        List<ProfileRole> roleList ) {
        public record ProfileRequestBot(
                @NotBlank
                String name,
                @NotBlank
                String surname,
                @NotBlank
                String password,
                @NotBlank
                List<ProfileRole> roleList
        ){

        }
}
