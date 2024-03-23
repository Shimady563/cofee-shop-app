package project.coffeeshop.authentication;

import com.lambdaworks.crypto.SCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;
import static project.coffeeshop.commons.ServletUtil.isValidSession;

@WebServlet(name = "ProfileServlet", value = "/profile")
public class ProfileServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            UUID sessionId = UUID.fromString(cookieOptional.get().getValue());

            Optional<Session> sessionOptional = sessionDao.findById(sessionId);

            if (sessionOptional.isPresent()) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUser().getId());
                userOptional.ifPresent(user -> webContext.setVariable("username", user.getUsername()));
            }
        }

        templateEngine.process("profile", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String newUsername = request.getParameter("newLogin");
        String newPassword = request.getParameter("newPassword");

        if (!newPassword.isBlank() && newPassword.length() < 3
                || newUsername.isBlank() || newUsername.length() < 3) {
            webContext.setVariable("message", "Username or password is too short");

            //calling doGet explicitly to show both username and message attributes on the page
            doGet(request, response);
            return;
        }

        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            UUID sessionId = UUID.fromString(cookieOptional.get().getValue());

            Optional<Session> sessionOptional = sessionDao.findById(sessionId);

            if (sessionOptional.isPresent() && isValidSession(sessionOptional.get(), LocalDateTime.now())) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUser().getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    boolean updated = false;

                    if (!user.getUsername().equals(newUsername)) {
                        user.setUsername(newUsername);
                        updated = true;
                    }

                    if (!newPassword.isBlank() && !SCryptUtil.check(newPassword, user.getPassword())) {
                        user.setPassword(SCryptUtil.scrypt(newPassword, 16, 16, 16));
                        updated = true;
                    }

                    webContext.setVariable("username", user.getUsername());

                    if (updated) {
                        webContext.setVariable("message", "Changes saved successfully");
                        userDao.update(user);
                    } else {
                        webContext.setVariable("message", "Username or password is the same");
                    }
                }
            }
        }

        //calling doGet explicitly to show both username and message attributes on the page
        doGet(request, response);
    }
}
