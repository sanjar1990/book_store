package api.kitabu.uz.dto.region;

import lombok.Builder;

@Builder
public record RegionLangResponse(
        Integer id,
        String name) {

}
