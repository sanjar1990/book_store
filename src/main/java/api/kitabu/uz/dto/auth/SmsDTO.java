package api.kitabu.uz.dto.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsDTO {

    private String phone;
    private String message;
    private String projectName;
    private String code;
}
