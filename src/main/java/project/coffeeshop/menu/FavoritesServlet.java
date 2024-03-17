package project.coffeeshop.menu;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;

@WebServlet(name = "FavoritesServlet", value = "/favorites")
public class FavoritesServlet extends CoffeeShopServlet {
    private SessionDao sessionDao;
    private MenuDao menuDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        sessionDao = new SessionDao();
        menuDao = new MenuDao();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                List<MenuItem> menuItems = menuDao.findByUserId(sessionOptional.get().getUser().getId());

                webContext.setVariable("menuItems", menuItems);
                templateEngine.process("favorites", webContext, response.getWriter());
                return;
            }
        }

        throw new ServletException("Couldn't load favorites");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        int menuItemId = Integer.parseInt(request.getParameter("menuItemId"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                menuDao.deleteUserFavorites(sessionOptional.get().getUser().getId(), menuItemId);
            }
        }

        doGet(request, response);
    }
}
