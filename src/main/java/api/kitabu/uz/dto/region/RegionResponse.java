package api.kitabu.uz.dto.region;

import api.kitabu.uz.enums.Lang;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record RegionResponse(
        Integer id,
        String nameUz,
        String nameRu,
        String nameEn,
        Boolean visible,
        LocalDateTime createdDate

) {
    public  RegionLangShortInfo shortInfo(){
        return new RegionLangShortInfo(
                this.nameUz,
                this.nameRu,
                this.nameEn);

    }
    public record RegionLangShortInfo(
            String nameUz,
            String nameRu,
            String nameEn
    ){}
}
