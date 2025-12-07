package api.kitabu.uz.exeptions;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler({
            ProfileException.class,
            PostNotFoundException.class
    })
    public ResponseEntity<?> handleException1(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
    @ExceptionHandler({ItemNotFoundException.class})
    public ResponseEntity<?> itemNotFoundException(Exception e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
    @ExceptionHandler({AppBadRequestException.class})
    public ResponseEntity<?> appBadHandleException(Exception e){return ResponseEntity.badRequest().body(e.getMessage());}
    @ExceptionHandler({GenreException.class})
    public ResponseEntity<?> GenreException(Exception ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }







}
