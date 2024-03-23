package project.coffeeshop.app;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.authentication.User;
import project.coffeeshop.authentication.UserDao;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;
import static project.coffeeshop.commons.ServletUtil.isValidSession;

@WebServlet(name = "HomeServlet", value = {"", "/home"})
public class HomeServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent() && isValidSession(sessionOptional.get(), LocalDateTime.now())) {
                webContext.setVariable("auth", true);

                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUser().getId());
                userOptional.ifPresent((user -> webContext.setVariable("username", user.getUsername())));
            }
        }

        templateEngine.process("index", webContext, response.getWriter());
    }
}
