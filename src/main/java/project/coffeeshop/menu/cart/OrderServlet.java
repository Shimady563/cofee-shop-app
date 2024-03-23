package project.coffeeshop.menu.cart;

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

@WebServlet(name = "OrderServlet", value = "/orders")
public class OrderServlet extends CoffeeShopServlet {
    private final SessionDao sessionDao = new SessionDao();
    private final OrderDao orderDao = new OrderDao();
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

                    webContext.setVariable("orders", orderDao.findByUser(user));
                    templateEngine.process("orders", webContext, response.getWriter());
                    return;
                }
            }
        }

        throw new ServletException("Failed to load orders");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long orderId = Long.parseLong(request.getParameter("orderId"));
        Optional<Order> orderOptional = orderDao.findById(orderId);

        if (orderOptional.isPresent()) {
            orderDao.delete(orderOptional.get());
        }

        response.sendRedirect(request.getContextPath() + "/orders");
    }
}
