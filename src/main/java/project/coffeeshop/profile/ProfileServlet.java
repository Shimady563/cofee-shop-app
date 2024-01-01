package project.coffeeshop.profile;

import com.lambdaworks.crypto.SCryptUtil;
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

@WebServlet(name = "ProfileServlet", value = "/profile")
public class ProfileServlet extends CoffeeShopServlet {
    private final UserDao userDao = new UserDao();
    private final SessionDao sessionDao = new SessionDao();

    public ProfileServlet() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            UUID sessionId = UUID.fromString(cookieOptional.get().getValue());

            Optional<Session> sessionOptional = sessionDao.findById(sessionId);

            if (sessionOptional.isPresent() && isValidSession(sessionOptional.get(), LocalDateTime.now())) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUserId());
                userOptional.ifPresent(user -> request.setAttribute("username", user.getUsername()));
            }
        }

        getServletContext().getRequestDispatcher("/profile.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String newUsername = request.getParameter("newLogin");
        Optional<String> newPassword = Optional.ofNullable(request.getParameter("newPassword"));

        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            UUID sessionId = UUID.fromString(cookieOptional.get().getValue());

            Optional<Session> sessionOptional = sessionDao.findById(sessionId);

            if (sessionOptional.isPresent() && isValidSession(sessionOptional.get(), LocalDateTime.now())) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUserId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    boolean updated = false;

                    if (!user.getUsername().equals(newUsername)) {
                        user.setUsername(newUsername);
                        updated = true;
                    }

                    if (newPassword.isPresent() && !SCryptUtil.check(newPassword.get(), user.getPassword())) {
                        user.setPassword(SCryptUtil.scrypt(newPassword.get(), 16, 16, 16));
                        updated = true;
                    }

                    if (updated) {
                        userDao.update(user);
                        response.sendRedirect(request.getContextPath() + "/profile");
                    }
                }
            }
        }
    }
}
