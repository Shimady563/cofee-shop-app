package project.coffeeshop.commons;

import jakarta.servlet.ServletException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.authentication.Session;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

public abstract class CoffeeShopServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            super.service(req, resp);
        } catch (ServletException e) {
            System.out.println(e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/error");
        }
    }

    public static Optional<Cookie> findCookieByName(Cookie[] cookies, String name) {
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    public static boolean isValidSession(Session session, LocalDateTime now) {
        return session.getExpirationTime().isAfter(now);
    }
}