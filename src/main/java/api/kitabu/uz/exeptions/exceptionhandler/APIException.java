package api.kitabu.uz.exeptions.exceptionhandler;

import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private final int status;

    public APIException(String message, int status) {
        super(message);
        this.status = status;
    }

    public APIException(String message) {
        super(message);
        this.status = 400;
    }

}
