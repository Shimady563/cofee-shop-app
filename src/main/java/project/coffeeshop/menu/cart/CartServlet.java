package project.coffeeshop.menu.cart;

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

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends CoffeeShopServlet {
    private SessionDao sessionDao;
    private CartDao cartDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        sessionDao = new SessionDao();
        cartDao = new CartDao();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                List<CartItem> cartItems = cartDao.findAll(sessionOptional.get().getUserId());
                webContext.setVariable("cartItems", cartItems);
                templateEngine.process("cart", webContext, response.getWriter());
            }
        }
    }
}
