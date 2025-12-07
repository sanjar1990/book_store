package api.kitabu.uz.dto.profile;

import api.kitabu.uz.enums.ProfileRole;

import java.util.List;

public record RoleChangeRequest(
        List<ProfileRole> roles
) {
}
