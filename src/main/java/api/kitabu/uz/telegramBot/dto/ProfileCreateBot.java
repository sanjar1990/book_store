package api.kitabu.uz.telegramBot.dto;

import api.kitabu.uz.enums.ProfileRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProfileCreateBot {
    private String firstName;
    private String lastName;
    private String password;
    private String phone;
    List<ProfileRole> roles = List.of(ProfileRole.ROLE_USER);
}
