package api.kitabu.uz.dto;



import api.kitabu.uz.enums.ProfileRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class JwtDTO {
    private  String phone;
    private ProfileRole role;
}
