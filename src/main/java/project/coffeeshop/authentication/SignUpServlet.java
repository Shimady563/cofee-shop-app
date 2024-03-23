package project.coffeeshop.authentication;

import com.lambdaworks.crypto.SCryptUtil;
import jakarta.servlet.ServletConfig;
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

@WebServlet(name = "SignUpServlet", value = "/sign-up")
public class SignUpServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        templateEngine.process("sign-up", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        if (password.length() < 3 || username.length() < 3) {
            webContext.setVariable("message", "Username or password is too short");
            templateEngine.process("sign-up", webContext, response.getWriter());
            return;
        }

        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isPresent()) {
            webContext.setVariable("message", "User already exists");
            templateEngine.process("sign-up", webContext, response.getWriter());
            return;
        }

        if (!password.equals(confirmPassword)) {
            webContext.setVariable("message", "Password mismatch");
            templateEngine.process("sign-up", webContext, response.getWriter());
            return;
        }

        User user = new User(username, SCryptUtil.scrypt(password, 16, 16, 16));
        userDao.save(user);
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId, LocalDateTime.now().plusHours(6), user);
        sessionDao.save(session);
        Cookie sessionCookie = new Cookie("sessionId", sessionId.toString());
        sessionCookie.setMaxAge(6*60*60);
        response.addCookie(sessionCookie);
        Optional<String> path = Optional.ofNullable((String) getServletContext().getAttribute("path"));
        response.sendRedirect(request.getContextPath() + path.orElse("/profile"));
    }
}
