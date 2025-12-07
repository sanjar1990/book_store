package api.kitabu.uz.dto.auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationDTO {
    @NotEmpty(message = "phone required")
    private String phone;
    @NotEmpty(message = "phone required")
    private String code;
}
