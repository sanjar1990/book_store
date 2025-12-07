package api.kitabu.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizationDTO {
    @NotBlank
    private String phone;
    @NotBlank
    private String password;
}
