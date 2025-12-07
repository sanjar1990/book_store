package api.kitabu.uz.dto.filter.post;

import api.kitabu.uz.enums.*;
import lombok.Builder;

@Builder
public record FilterRequest(
        String title,
        Integer regionId,
        ExchangeType exchangeType,
        ConditionType conditionType,
        BookLanguage bookLanguage,
        BookPrintType bookPrintType,
        String genreId,
        String profileId,
        GeneralStatus status
) {
}
