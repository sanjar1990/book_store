package api.kitabu.uz.dto;

public record Response<T>(
        boolean isError,
        String message,
        int status,
        T data
) {
    public static <T> Response<T> ok(T data) {
        return new Response<>(false, "", 200, data);
    }
    public static <T extends String> Response<T> notFound(T message) {
        return new Response<>(true, message, 404, null);
    }

    public static <T extends String> Response<T> badRequest(T message) {
        return new Response<>(true, message, 400, null);
    }

    public static <T> Response<T> fieldError(T errors) {
        return new Response<>(true, "error", 400, errors);
    }

    public static <T extends String> Response<T> serverError(T message) {
        return new Response<>(true, message, 500, null);
    }
}
