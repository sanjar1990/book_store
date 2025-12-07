package api.kitabu.uz.mappers;

import api.kitabu.uz.enums.GeneralStatus;
import api.kitabu.uz.enums.ProfileRole;

import java.time.LocalDateTime;
import java.util.List;

public interface ProfileMapper {
    String getId();
    String getName();
    String getSurname();
    String getPhone();
    String getPassword();
    String getPhotoId();
    GeneralStatus getStatus();
    LocalDateTime getCreatedDate();
}
