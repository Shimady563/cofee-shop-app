package project.coffeeshop.menu;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Hibernate;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.authentication.User;
import project.coffeeshop.authentication.UserDao;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;

@WebServlet(name = "FavoritesServlet", value = "/favorites")
public class FavoritesServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final MenuDao menuDao = new MenuDao();
    private final UserDao userDao = new UserDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUser().getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    userDao.refresh(user);
                    Hibernate.initialize(user.getFavorites());
                    webContext.setVariable("menuItems", user.getFavorites());
                    templateEngine.process("favorites", webContext, response.getWriter());
                    return;
                }
            }
        }

        throw new ServletException("Couldn't load favorites");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long menuItemId = Integer.parseInt(request.getParameter("menuItemId"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUser().getId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    userDao.refresh(user);
                    Hibernate.initialize(user.getFavorites());
                    Optional<MenuItem> menuItemOptional = menuDao.findById(menuItemId);

                    if (menuItemOptional.isPresent()) {
                        user.removeFromFavorites(menuItemOptional.get());
                        userDao.update(user);
                    }
                }
            }
        }

        doGet(request, response);
    }
}
