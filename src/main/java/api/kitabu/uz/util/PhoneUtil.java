package api.kitabu.uz.util;

import api.kitabu.uz.exeptions.exceptionhandler.APIException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {
    public static String correctPhone(String phone) {
        if (!phone.startsWith("+998")) {
            phone = "+998" + phone;
        }
        String pattern = "^\\+998\\d{9}$";
        Pattern compiledPattern = Pattern.compile(pattern);
        Matcher matcher = compiledPattern.matcher(phone);
        if (matcher.matches()) {
            return phone;
        } else {
            throw new APIException("Phone number is incorrect");
        }
    }
}
