package api.kitabu.uz.dto.profile;

import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.ProfileRole;
import lombok.Builder;

import java.util.List;

@Builder
public record ProfileRequestUpdate(
        String name,
        String surname,
        String password,
        GeneralStatus status,
        List<ProfileRole> roleList) {
}
