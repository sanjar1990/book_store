package api.kitabu.uz.exeptions.exceptionhandler;

import lombok.Builder;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Builder
public record ErrorResponse(
        String message,
        HttpStatus status,
        Integer statusCode,
        ZonedDateTime timestamp

) {
}
