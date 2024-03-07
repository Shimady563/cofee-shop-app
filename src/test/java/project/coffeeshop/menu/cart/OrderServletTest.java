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

        Field webContextField = orderServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(orderServlet, webContext);

        Field templateEngineField = orderServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(orderServlet, templateEngine);
    }

    @Test
    public void doGet_Should_RenderOrdersTemplate_When_SessionExists() throws ServletException, IOException {
        List<Order> orderList = new ArrayList<>();
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(orderDao.findAll(any(Long.class))).thenReturn(orderList);

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
    public void doGet_Should_ThrowServletException_When_SessionDoesNotExist() throws ServletException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> orderServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_DeleteOrderAndRedirectToOrders_When_EverythingIsFine() throws ServletException, IOException {
        long orderId = 1L;
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getParameter("orderId")).thenReturn(String.valueOf(orderId));
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        orderServlet.doPost(request, response);

        verify(orderDao, times(1)).delete(any(Long.class), eq(orderId));
        verify(response, times(1)).sendRedirect(anyString());
    }

    @Test
    public void doPost_Should_ThrowServletException_When_CookieNotFound() {
        when(request.getParameter("orderId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> orderServlet.doPost(request, response));
    }

    @Test
    public void doPost_Should_ThrowServletException_When_SessionDoesNotExist() throws ServletException {
        when(request.getParameter("orderId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> orderServlet.doPost(request, response));
    }
}
