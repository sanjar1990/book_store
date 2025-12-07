package api.kitabu.uz.dto.filter.user;

import api.kitabu.uz.enums.ProfileRole;
import lombok.Builder;

@Builder
public record UserFilterRequest(
String nameOrSurname,
String phone,
ProfileRole role
) {
}
