package project.coffeeshop.menu.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.authentication.User;
import project.coffeeshop.commons.CoffeeShopServlet;
import project.coffeeshop.menu.MenuDao;
import project.coffeeshop.menu.MenuItem;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;

@WebServlet(name = "CartServlet", value = "/cart")
public class CartServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final CartDao cartDao = new CartDao();
    private final MenuDao menuDao = new MenuDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                List<UserCart> cartItems = cartDao.findByUser(sessionOptional.get().getUser());

                double overall = cartItems
                        .stream()
                        .mapToDouble((item) -> (item.getQuantity() * item.getMenuItem().getPrice()))
                        .sum();

                //rounding to 2 digits after the decimal point
                double scale = Math.pow(10, 2);
                overall = Math.round(overall * scale) / scale;

                webContext.setVariable("cartItems", cartItems);
                webContext.setVariable("overall", overall);
                templateEngine.process("cart", webContext, response.getWriter());
                return;
            }
        }

        throw new ServletException("Failed to load cart items");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        long cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                User user = sessionOptional.get().getUser();
                Optional<MenuItem> menuItemOptional = menuDao.findById(cartItemId);

                if (menuItemOptional.isPresent()) {
                    MenuItem menuItem = menuItemOptional.get();
                    UserCart userCart = new UserCart(user, menuItem);

                    switch (action) {
                        case "add" -> {
                            cartDao.save(userCart);
                            response.sendRedirect(request.getParameter("path"));
                            return;
                        }
                        case "decrease" -> {
                            int newQuantity = Integer.parseInt(request.getParameter("oldQuantity")) - 1;
                            if (newQuantity == 0) {
                                cartDao.deleteByUserAndItem(user, menuItem);
                            } else {
                                userCart.setQuantity(newQuantity);
                                cartDao.update(userCart);
                            }
                        }
                        case "increase" -> {
                            int newQuantity = Integer.parseInt(request.getParameter("oldQuantity")) + 1;
                            userCart.setQuantity(newQuantity);
                            cartDao.update(userCart);
                        }

                        case "remove" -> cartDao.deleteByUserAndItem(user, menuItem);
                    }
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
}
