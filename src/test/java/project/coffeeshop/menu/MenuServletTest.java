package project.coffeeshop.menu;

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
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MenuServletTest {
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
    private MenuDao menuDao;
    @Mock
    private UserDao userDao;

    private MenuServlet menuServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        menuServlet = new MenuServlet();

        Field sessionDaoField = menuServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(menuServlet, sessionDao);

        Field menuDaoField = menuServlet.getClass().getDeclaredField("menuDao");
        menuDaoField.setAccessible(true);
        menuDaoField.set(menuServlet, menuDao);

        Field userDaoField = menuServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(menuServlet, userDao);

        Field webContextField = menuServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(menuServlet, webContext);

        Field templateEngineField = menuServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(menuServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute_When_MenuItemListIsNotEmpty() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem( "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItemList = List.of(menuItem);

        when(menuDao.findAll()).thenReturn(menuItemList);

        menuServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("menuItems"), eq(menuItemList));
        verify(templateEngine, times(1)).process(eq("menu"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ThrowException_When_MenuItemListIsEmpty() throws ServletException {
        when(menuDao.findAll()).thenReturn(List.of());

        assertThrows(ServletException.class, () -> menuServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_SaveItemToFavoritesAndReloadThePage_When_EverythingIsFine() throws ServletException, NoSuchFieldException, IllegalAccessException {
        MenuItem menuItem = new MenuItem( "menuItem", 0.0, 0, "path");
        Set<MenuItem> menuItems = new HashSet<>();
        menuItems.add(menuItem);
        User user = new User();
        Field favorites = user.getClass().getDeclaredField("favorites");
        favorites.setAccessible(true);
        favorites.set(user, menuItems);
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        assertThrows(ServletException.class, () -> menuServlet.doPost(request, response));

        verify(userDao, times(1)).update(eq(user));
        verify(webContext, times(1)).setVariable(eq("itemId"), eq(1L));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Added to favorites"));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutSaving_When_CookieNotFound() throws ServletException, IOException {
        User user = new User();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> menuServlet.doPost(request, response));

        verify(sessionDao, never()).findById(eq(session.getId()));
        verify(userDao, never()).findById(eq(user.getId()));
        verify(userDao, never()).update(eq(user));
        verify(webContext, never()).setVariable(eq("itemId"), eq(1));
        verify(webContext, never()).setVariable(eq("message"), eq("Added to favorites"));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutSaving_When_SessionNotFound() throws ServletException, IOException {
        User user = new User();

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> menuServlet.doPost(request, response));

        verify(userDao, never()).findById(eq(user.getId()));
        verify(userDao, never()).update(eq(user));
        verify(webContext, never()).setVariable(eq("itemId"), eq(1));
        verify(webContext, never()).setVariable(eq("message"), eq("Added to favorites"));
    }
}
