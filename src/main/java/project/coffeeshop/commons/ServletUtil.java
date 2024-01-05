package project.coffeeshop.commons;

import jakarta.servlet.http.Cookie;
import project.coffeeshop.authentication.Session;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public class ServletUtil {
    public static Optional<Cookie> findCookieByName(Cookie[] cookies, String name) {
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    public static boolean isValidSession(Session session, LocalDateTime now) {
        return session.getExpirationTime().isAfter(now);
    }

    public static String parsePath(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf('/'));
    }
}
