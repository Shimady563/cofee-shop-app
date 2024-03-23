package project.coffeeshop.menu.cart;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.authentication.User;
import project.coffeeshop.authentication.UserDao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private WebContext webContext;
    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private SessionDao sessionDao;
    @Mock
    private OrderDao orderDao;
    @Mock
    private UserDao userDao;

    private OrderServlet orderServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        orderServlet = new OrderServlet();

        Field sessionDaoField = orderServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(orderServlet, sessionDao);

        Field orderDaoField = orderServlet.getClass().getDeclaredField("orderDao");
        orderDaoField.setAccessible(true);
        orderDaoField.set(orderServlet, orderDao);

        Field userDaoField = orderServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(orderServlet, userDao);

        Field webContextField = orderServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(orderServlet, webContext);

        Field templateEngineField = orderServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(orderServlet, templateEngine);
    }

    @Test
    public void doGet_Should_RenderOrdersTemplate_When_SessionExists() throws ServletException, IOException {
        User user = new User();
        List<Order> orderList = new ArrayList<>();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));
        when(orderDao.findByUser(any())).thenReturn(orderList);

        orderServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("orders"), eq(orderList));
        verify(templateEngine, times(1)).process(eq("orders"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ThrowServletException_When_CookieNotFound() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> orderServlet.doGet(request, response));
    }

    @Test
    public void doGet_Should_ThrowServletException_When_SessionDoesNotExist() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> orderServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_DeleteOrderAndRedirectToOrders_When_EverythingIsFine() throws ServletException, IOException {
        long orderId = 1L;
        Order order = new Order();

        when(request.getParameter("orderId")).thenReturn(String.valueOf(orderId));
        when(orderDao.findById(any())).thenReturn(Optional.of(order));

        orderServlet.doPost(request, response);

        verify(orderDao, times(1)).delete(eq(order));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/orders"));
    }
}
