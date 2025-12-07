package api.kitabu.uz.dto.profile;

import api.kitabu.uz.enums.ProfileRole;

import java.util.List;

public record ProfileResponseDetail(
        String name,
        String surname,
        String phone,
        String photoId,
        List<ProfileRole> roleList) {

}
