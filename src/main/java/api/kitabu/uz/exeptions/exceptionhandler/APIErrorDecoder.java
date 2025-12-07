package api.kitabu.uz.exeptions.exceptionhandler;

import feign.Response;
import feign.codec.ErrorDecoder;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class APIErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        Response.Body responseBody = response.body();
        String message;

        try {
            message = getStringFromStream(responseBody.asInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new APIException(message, response.status());
    }

    private String getStringFromStream(InputStream stream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(stream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();

        for (int result = bis.read(); result != -1; result = bis.read()) {
            buf.write((byte) result);
        }

        return buf.toString(StandardCharsets.UTF_8);
    }

}
