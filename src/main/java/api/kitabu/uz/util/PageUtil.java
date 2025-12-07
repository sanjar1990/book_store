package api.kitabu.uz.util;

public class PageUtil {
    public static int getPage(int page) {
        if(page <= 0){
            return 0;
        }else {
            return page - 1;
        }
    }
}
