package api.kitabu.uz.dto.profile;

import api.kitabu.uz.enums.ProfileRole;
import api.kitabu.uz.enums.GeneralStatus;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;
@Builder
public record ProfileResponse(
        String id,
        String name,
        String surname,
        String phone,
        String password,
        String photoId,
        GeneralStatus status,
        List<ProfileRole> roleList,
        LocalDateTime createdDate) {
    public record ProfileResponseShort(
            String id,
            String name,
            String surname,
            String phone,
            String url
    ) {
    }
    @Builder
    public record ProfileShort(
            String name,
            String surname
    ) {
    }


}
