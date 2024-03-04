package project.coffeeshop.menu.cart;

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
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;

@WebServlet(name = "PurchaseServlet", value = "/purchase")
public class PurchaseServlet extends CoffeeShopServlet {
    private SessionDao sessionDao;
    private CartDao cartDao;
    private UserDao userDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        sessionDao = new SessionDao();
        cartDao = new CartDao();
        userDao = new UserDao();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        double overallPrice = Double.parseDouble(request.getParameter("overall"));
        webContext.setVariable("overall", overallPrice);
        templateEngine.process("purchase", webContext, response.getWriter());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        double overallPrice = Double.parseDouble(request.getParameter("overall"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                Optional<User> userOptional = userDao.findById(sessionOptional.get().getUserId());

                if (userOptional.isPresent()) {
                    User user = userOptional.get();
                    int pointsEarned = (int) overallPrice / 10;
                    user.setPoints(user.getPoints() + pointsEarned);
                    cartDao.deleteAll(user.getId());
                    userDao.update(user.getId(), user.getPoints());
                    //implement order creation, redirection to page with success message
                }
            }
        }
    }
}