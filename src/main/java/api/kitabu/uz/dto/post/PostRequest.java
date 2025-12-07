package api.kitabu.uz.dto.post;

import api.kitabu.uz.entity.PostEntity;
import api.kitabu.uz.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.security.core.parameters.P;

import java.util.List;

@Builder
public record PostRequest(
        @NotBlank(message = "Title Type required")
        String title,
        @NotBlank(message = "Description Type required")
        String description,
        @NotNull(message = "Exchange Type required")
        ExchangeType exchangeType,
        @NotNull(message = "Condition Type required")
        ConditionType conditionType,
        @NotNull(message = "BookLanguage required")
        BookLanguage bookLanguage,
        @NotNull(message = "BookPrintType required")
        BookPrintType bookPrintType,
        @NotBlank(message = "Author name required")
        String authorName,
        Double latitude,
        Double longitude,
        Double price,
        Double marketPrice,
        Integer regionId,
        List<String> attachIdList,
        List<String> genreIdList
) {
        public record PostRequestShort(
                @NotBlank(message = "Title Type required")
                String title,
                @NotBlank(message = "Description Type required")
                String description,
                @NotNull(message = "Exchange Type required")
                ExchangeType exchangeType,
                @NotNull(message = "Condition Type required")
                ConditionType conditionType,
                @NotNull(message = "BookLanguage required")
                BookLanguage bookLanguage,
                @NotNull(message = "BookPrintType required")
                BookPrintType bookPrintType,
                @NotBlank(message = "Author name required")
                String authorName,
                Integer regionId,
                List<String> attachIdList,
                List<String> genreIdList
        ){

        }
}