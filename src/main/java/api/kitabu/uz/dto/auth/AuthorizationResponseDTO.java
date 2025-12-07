package api.kitabu.uz.dto.auth;


import api.kitabu.uz.dto.FileResponse;
import api.kitabu.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AuthorizationResponseDTO{
   private String id;
   private String name;
   private String surname;
   private List<ProfileRole> roles;
   private String token;
   private String phone;
   private FileResponse photo;
}
