package api.kitabu.uz.exeptions;

public class AppBadRequestException extends RuntimeException{
    public AppBadRequestException(String message){
        super(message);
    }
}
