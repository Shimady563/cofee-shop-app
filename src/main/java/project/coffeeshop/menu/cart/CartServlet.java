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
import project.coffeeshop.menu.MenuItem;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Comparator;
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

                //sorting the list so that after changing the quantity items doesn't rearrange
                cartItems.sort(Comparator.comparing(MenuItem::getName).thenComparing(MenuItem::getVolume));

                double overall = cartItems
                        .stream()
                        .mapToDouble((item) -> (item.getQuantity() * item.getPrice()))
                        .sum();

                //rounding to 2 digits after the decimal point
                double scale = Math.pow(10, 2);
                overall = Math.round(overall * scale) / scale;

                webContext.setVariable("cartItems", cartItems);
                webContext.setVariable("overall",  overall);
                templateEngine.process("cart", webContext, response.getWriter());
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        int cartItemId = Integer.parseInt(request.getParameter("cartItemId"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                long userId = sessionOptional.get().getUserId();

                switch (action) {
                    case "add" -> {
                        cartDao.saveToCart(userId, cartItemId);
                        response.sendRedirect(request.getParameter("path"));
                        return;
                    }
                    case "decrease" -> {
                        int newQuantity = Integer.parseInt(request.getParameter("oldQuantity")) - 1;
                        if (newQuantity == 0) {
                            cartDao.deleteItem(userId, cartItemId);
                        } else {
                            cartDao.updateQuantity(userId, cartItemId, newQuantity);
                        }
                    }
                    case "increase" -> {
                        int newQuantity = Integer.parseInt(request.getParameter("oldQuantity")) + 1;
                        cartDao.updateQuantity(userId, cartItemId, newQuantity);
                    }
                    case "remove" -> cartDao.deleteItem(userId, cartItemId);
                }
            }
        }

        doGet(request, response);
    }
}
