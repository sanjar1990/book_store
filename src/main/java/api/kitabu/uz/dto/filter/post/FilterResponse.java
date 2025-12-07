package api.kitabu.uz.dto.filter.post;

import api.kitabu.uz.dto.FileResponse;
import api.kitabu.uz.enums.BookPrintType;
import api.kitabu.uz.enums.ExchangeType;
import api.kitabu.uz.enums.GeneralStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record FilterResponse(
        String postId,
        ExchangeType exchangeType,
        LocalDateTime createdDate,
        String regionName,
        String genreNames,
        String authorName,
        FileResponse attach,
        String title,
        GeneralStatus status,
        BookPrintType printType,
        Double price) {
}
