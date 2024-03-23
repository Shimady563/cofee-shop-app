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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.findCookieByName;

@WebServlet(name = "PurchaseServlet", value = "/purchase")
public class PurchaseServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final CartDao cartDao = new CartDao();
    private final OrderDao orderDao = new OrderDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        double overallPrice = Double.parseDouble(request.getParameter("overall"));
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

            if (sessionOptional.isPresent()) {
                User user = sessionOptional.get().getUser();
                Order order = new Order(LocalDateTime.now(), LocalDateTime.now().plusMinutes(10), overallPrice, user);
                orderDao.save(order);
                cartDao.deleteByUser(user);

                templateEngine.process("purchase", webContext, response.getWriter());
                return;
            }

        }

        throw new ServletException("Couldn't create an order");
    }
}
