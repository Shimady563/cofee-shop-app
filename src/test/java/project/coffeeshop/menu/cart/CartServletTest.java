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
import project.coffeeshop.menu.MenuDao;
import project.coffeeshop.authentication.User;
import project.coffeeshop.menu.MenuItem;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @Mock
    private MenuDao menuDao;

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

        Field menuDaoField = cartServlet.getClass().getDeclaredField("menuDao");
        menuDaoField.setAccessible(true);
        menuDaoField.set(cartServlet, menuDao);

        Field webContextField = cartServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(cartServlet, webContext);

        Field templateEngineField = cartServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(cartServlet, templateEngine);
    }

    @Test
    public void doGet_Should_RenderCartTemplate_When_SessionExists() throws ServletException, IOException {
        User user = new User();
        MenuItem menuItem = new MenuItem( "m", 123, 12354, "p");
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);
        List<UserCart> cartItems = List.of(new UserCart(user, menuItem));

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(cartDao.findByUser(any())).thenReturn(cartItems);

        cartServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("cartItems"), eq(cartItems));
        verify(templateEngine, times(1)).process(eq("cart"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ThrowServletException_When_CookieNotFound() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> cartServlet.doGet(request, response));
    }

    @Test
    public void doGet_Should_ThrowServletException_When_SessionDoesNotExist() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> cartServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_AddItemToCartAndRedirectToPath_When_ActionIsAdd() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        User user = new User();
        MenuItem menuItem = new MenuItem( );
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("action")).thenReturn("add");
        when(request.getParameter("cartItemId")).thenReturn(String.valueOf(1));
        when(request.getParameter("path")).thenReturn("path");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        cartServlet.doPost(request, response);

        verify(response, times(1)).sendRedirect(eq("path"));
    }

    @Test
    public void doPost_Should_DecreaseQuantityAndRedirectToCart_When_ActionIsDecreaseAndNewQuantityDoesNotEqualsTo0() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        User user = new User();
        MenuItem menuItem = new MenuItem( );
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("action")).thenReturn("decrease");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("oldQuantity")).thenReturn("2");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        cartServlet.doPost(request, response);

        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }

    @Test
    public void doPost_Should_DeleteItemAndRedirectToCart_When_ActionIsDecreaseAndNewQuantityEqualsTo0() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        User user = new User();
        MenuItem menuItem = new MenuItem();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("action")).thenReturn("decrease");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("oldQuantity")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).deleteByUserAndItem(eq(user), eq(menuItem));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }

    @Test
    public void doPost_Should_IncreaseQuantityAndRedirectToCart_When_ActionIsIncrease() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        User user = new User();
        MenuItem menuItem = new MenuItem();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("action")).thenReturn("increase");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getParameter("oldQuantity")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        cartServlet.doPost(request, response);

        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }

    @Test
    public void doPost_Should_DeleteItemAndRedirectToCart_When_ActionIsRemove() throws ServletException, IOException {
        User user = new User();
        MenuItem menuItem = new MenuItem();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("action")).thenReturn("remove");
        when(request.getParameter("cartItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        cartServlet.doPost(request, response);

        verify(cartDao, times(1)).deleteByUserAndItem(eq(user), eq(menuItem));
        verify(response, times(1)).sendRedirect(eq(request.getContextPath() + "/cart"));
    }
}
