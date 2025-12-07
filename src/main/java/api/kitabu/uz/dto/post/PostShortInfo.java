package api.kitabu.uz.dto.post;

import api.kitabu.uz.enums.ExchangeType;
import api.kitabu.uz.enums.GeneralStatus;

import java.time.LocalDateTime;


public interface PostShortInfo {
    String getPostId();
    String getTitle();
    ExchangeType getType();
    LocalDateTime getCreatedDate();
    String getGenreNames();
    String getAuthorName();
    String getAttachId();
    String getRegionName();
    GeneralStatus getStatus();


}
