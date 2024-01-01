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

@WebServlet(name = "SignInServlet", value = "/sign-in")
public class SignInServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final UserDao userDao = new UserDao();

    public SignInServlet() throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/sign-in.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Optional<User> userOptional = userDao.findByUsername(username);

        if (userOptional.isEmpty()) {
            request.setAttribute("message", "User not found");
            getServletContext().getRequestDispatcher("/sign-in.jsp").forward(request, response);
            return;
        }

        User user = userOptional.get();

        if (!SCryptUtil.check(password, user.getPassword())) {
            request.setAttribute("message", "Wrong password");
            getServletContext().getRequestDispatcher("/sign-in.jsp").forward(request, response);
            return;
        }

        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId, user.getId(), LocalDateTime.now().plusHours(6));
        sessionDao.save(session);
        Cookie sessionCookie = new Cookie("sessionId", sessionId.toString());
        sessionCookie.setMaxAge(6*60*60);
        response.addCookie(sessionCookie);
        Optional<String> path = Optional.ofNullable((String) request.getAttribute("path"));
        response.sendRedirect(request.getContextPath() + path.orElse("/profile"));
    }
}
