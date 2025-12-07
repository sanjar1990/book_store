package api.kitabu.uz.exeptions.exceptionhandler;

import api.kitabu.uz.dto.ApiResponse;
import api.kitabu.uz.exeptions.ProfileException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.security.SignatureException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class APIExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request
    ) {

        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }

//    @ExceptionHandler({APIException.class})
//    public ResponseEntity<ErrorResponse> apiException(APIException exception) {
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .message(exception.getMessage())
//                .status(HttpStatus.valueOf(exception.getStatus()))
//                .statusCode(exception.getStatus())
//                .timestamp(ZonedDateTime.now(ZoneId.of("Z")))
//                .build();
//        return new ResponseEntity<>(errorResponse, errorResponse.status());
//    }

    @ExceptionHandler({APIException.class})
    public ResponseEntity<ApiResponse<String>> apiException(APIException exception) {
        return ResponseEntity.ok(ApiResponse.bad(exception.getMessage()));
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<?> handler(RuntimeException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }

    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<?> handler(SignatureException exception) {
        return new ResponseEntity<>(ApiResponse.unAuthorized("Token not valid"), HttpStatus.UNAUTHORIZED);
    }


}
