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

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PurchaseServletTest {
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
    private CartDao cartDao;
    @Mock
    private OrderDao orderDao;

    private PurchaseServlet purchaseServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        purchaseServlet = new PurchaseServlet();

        Field sessionDaoField = purchaseServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(purchaseServlet, sessionDao);

        Field cartDaoField = purchaseServlet.getClass().getDeclaredField("cartDao");
        cartDaoField.setAccessible(true);
        cartDaoField.set(purchaseServlet, cartDao);

        Field orderDaoField = purchaseServlet.getClass().getDeclaredField("orderDao");
        orderDaoField.setAccessible(true);
        orderDaoField.set(purchaseServlet, orderDao);

        Field webContextField = purchaseServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(purchaseServlet, webContext);

        Field templateEngineField = purchaseServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(purchaseServlet, templateEngine);
    }

    @Test
    public void doPost_Should_CreateOrder_When_EverythingIsFine() throws ServletException, IOException {
        User user = new User();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);
        double overallPrice = 10.0;

        when(request.getParameter("overall")).thenReturn(String.valueOf(overallPrice));
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        purchaseServlet.doPost(request, response);

        verify(cartDao, times(1)).deleteByUser(eq(user));
        verify(templateEngine, times(1)).process("purchase", webContext, response.getWriter());
    }

    @Test
    public void doPost_Should_ThrowServletException_When_CookieNotFound() {
        when(request.getParameter("overall")).thenReturn("1.0");
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> purchaseServlet.doPost(request, response));
    }

    @Test
    public void doPost_Should_ThrowServletException_When_SessionNotFound() throws ServletException {
        when(request.getParameter("overall")).thenReturn("1.0");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> purchaseServlet.doPost(request, response));
    }
}
