package project.coffeeshop.menu;

import jakarta.servlet.ServletConfig;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;

@WebServlet(name = "MenuServlet", value = "/menu")
public class MenuServlet extends CoffeeShopServlet {
    private MenuDao menuDao;
    private SessionDao sessionDao;
    private final UserDao userDao = new UserDao();

    @Override
    public void init(ServletConfig config) throws ServletException {
        menuDao = new MenuDao();
        sessionDao = new SessionDao();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MenuItem> menuItems = menuDao.findAll();
        if (menuItems.isEmpty()) {
            throw new ServletException("Menu items list is empty");
        }

        webContext.setVariable("menuItems", menuItems);
        templateEngine.process("menu", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long menuItemId = Integer.parseInt(request.getParameter("menuItemId"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");
        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                User user = sessionOptional.get().getUser();
                Hibernate.initialize(user.getFavorites());

                Optional<MenuItem> menuItemOptional = menuDao.findById(menuItemId);

                if (menuItemOptional.isPresent()) {
                    user.addToFavorites(menuItemOptional.get());
                    System.out.println(user.getFavorites());
                    userDao.update(user);
                    webContext.setVariable("itemId", menuItemId);
                    webContext.setVariable("message", "Added to favorites");
                }
            }
        }

        doGet(request, response);
    }
}
