package project.coffeeshop.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@WebServlet(name = "SignOutServlet", value = "/sign-out")
public class SignOutServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();

    public SignOutServlet() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Cookie sessionCookie = cookieOptional.get();
            UUID sessionId = UUID.fromString(sessionCookie.getValue());

            sessionDao.delete(sessionId);
            sessionCookie.setMaxAge(0);
            response.addCookie(sessionCookie);
        }

        response.sendRedirect(request.getContextPath() + "/home");
    }
}
