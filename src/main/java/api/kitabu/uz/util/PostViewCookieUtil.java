package api.kitabu.uz.util;



import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

public class PostViewCookieUtil {
    public static final String VIEWED_POST_COOKIE_PREFIX = "viewed_post_";
    public static boolean hasViewedPost(HttpServletRequest request, String postId) {
        if (request.getCookies() == null) {
            return false;
        }
        String cookieName = VIEWED_POST_COOKIE_PREFIX + postId;
        return Arrays.stream(request.getCookies())
                .anyMatch(cookie -> cookie.getName().equals(cookieName));
    }

    public static void addViewedPostCookie(HttpServletResponse response, String postId) {
        String cookieName = VIEWED_POST_COOKIE_PREFIX + postId;
        Cookie cookie = new Cookie(cookieName, "true");
        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);
        response.addCookie(cookie);
    }
}
