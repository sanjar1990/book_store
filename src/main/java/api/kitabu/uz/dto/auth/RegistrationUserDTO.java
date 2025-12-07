package api.kitabu.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationUserDTO {
    @NotBlank
//    @Size(message = "Max length 15",max = 15,min = 3)
    private String name;
    @NotBlank
    private String surname;
    @Size(message = "Max or min phone", max = 13)
    private String phone;
    @NotBlank
    private String password;
    private String photoId;
    private String signature;

}
