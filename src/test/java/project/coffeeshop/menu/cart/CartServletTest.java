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
public class CartServletTest {
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

    private CartServlet cartServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        cartServlet = new CartServlet();

        Field sessionDaoField = cartServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(cartServlet, sessionDao);

        Field cartDaoField = cartServlet.getClass().getDeclaredField("cartDao");
        cartDaoField.setAccessible(true);
        cartDaoField.set(cartServlet, cartDao);

        Field webContextField = cartServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(cartServlet, webContext);

        Field templateEngineField = cartServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(cartServlet, templateEngine);
    }

    @Test
    public void doGet_Should_RenderCartTemplate_When_SessionExists() throws ServletException, IOException {
        CartItem cartItem = new CartItem();
        cartItem.setQuantity(1);
        cartItem.setPrice(2.0);
        List<CartItem> cartItemList = new ArrayList<>();
        cartItemList.add(cartItem);
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(cartDao.findAll(any(Long.class))).thenReturn(cartItemList);

        cartServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("cartItems"), eq(cartItemList));
        verify(webContext, times(1)).setVariable(eq("overall"), eq(cartItem.getQuantity() * cartItem.getPrice()));
        verify(templateEngine, times(1)).process(eq("cart"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ThrowServletException_When_CookieNotFound() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> cartServlet.doGet(request, response));
    }

    @Test
    public void doGet_Should_ThrowServletException_When_SessionDoesNotExist() throws ServletException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> cartServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_AddItemToCartAndRedirectToPath_When_ActionIsAdd() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        session.setUserId(1L);

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("path")).thenReturn("path");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).saveToCart(eq(1L), eq(1L));
        verify(response, times(1)).sendRedirect(eq("path"));
    }

    @Test
    public void doPost_Should_DecreaseQuantityAndRedirectToCart_When_ActionIsDecreaseAndNewQuantityDoesNotEqualsTo0() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        session.setUserId(1L);

        when(request.getParameter("action")).thenReturn("decrease");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("oldQuantity")).thenReturn("2");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).updateQuantity(eq(1L), eq(1L), eq(1));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }

    @Test
    public void doPost_Should_DeleteItemAndRedirectToCart_When_ActionIsDecreaseAndNewQuantityEqualsTo0() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        session.setUserId(1L);

        when(request.getParameter("action")).thenReturn("decrease");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("oldQuantity")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).deleteItem(eq(1L), eq(1L));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }

    @Test
    public void doPost_Should_IncreaseQuantityAndRedirectToCart_When_ActionIsIncrease() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        session.setUserId(1L);

        when(request.getParameter("action")).thenReturn("increase");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("oldQuantity")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).updateQuantity(eq(1L), eq(1L), eq(2));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }

    @Test
    public void doPost_Should_DeleteItemAndRedirectToCart_When_ActionIsRemove() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        session.setUserId(1L);

        when(request.getParameter("action")).thenReturn("remove");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).deleteItem(eq(1L), eq(1L));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }
}
