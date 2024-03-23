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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
public class FavoritesServletTest {
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

    private FavoritesServlet favoritesServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        favoritesServlet = new FavoritesServlet();

        Field sessionDaoField = favoritesServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(favoritesServlet, sessionDao);

        Field menuDaoField = favoritesServlet.getClass().getDeclaredField("menuDao");
        menuDaoField.setAccessible(true);
        menuDaoField.set(favoritesServlet, menuDao);

        Field userDaoField = favoritesServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(favoritesServlet, userDao);

        Field webContextField = favoritesServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(favoritesServlet, webContext);

        Field templateEngineField = favoritesServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(favoritesServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute_When_EverythingIsFine() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        Set<MenuItem> menuItems = new HashSet<>();
        User user = new User();
        Field favorites = user.getClass().getDeclaredField("favorites");
        favorites.setAccessible(true);
        favorites.set(user, menuItems);
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        favoritesServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("menuItems"), eq(menuItems));
        verify(templateEngine, times(1)).process(eq("favorites"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ThrowAnException_When_CookieNotFound() {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        assertThrows(ServletException.class, () -> favoritesServlet.doGet(request, response));
    }

    @Test
    public void doGet_Should_ThrowAnException_When_SessionNotFound() {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> favoritesServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_DeleteItemFromFavoritesAndReloadThePage_When_EverythingIsFine() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        MenuItem menuItem = new MenuItem( "menuItem", 0.0, 0, "path");
        Set<MenuItem> menuItems = new HashSet<>();
        menuItems.add(menuItem);
        User user = new User();
        Field favorites = user.getClass().getDeclaredField("favorites");
        favorites.setAccessible(true);
        favorites.set(user, menuItems);
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MAX, user);
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));
        when(menuDao.findById(any())).thenReturn(Optional.of(menuItem));

        favoritesServlet.doPost(request, response);

        verify(userDao, times(1)).update(eq(user));
        assertEquals(menuItems.size(), 0);
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutDeleting_When_CookieNotFound() throws ServletException {
        MenuItem menuItem = new MenuItem( "menuItem", 0.0, 0, "path");
        User user = new User();
        Session session = new Session(UUID.randomUUID(), LocalDateTime.MIN, user);

        when(request.getCookies()).thenReturn(new Cookie[]{});
        when(request.getParameter("menuItemId")).thenReturn("1");

        //catching exception because of doGet
        // probably very bad, but I can't figure out how to do it without this
        assertThrows(ServletException.class, () -> favoritesServlet.doPost(request, response));

        verify(sessionDao, never()).findById(eq(session.getId()));
        verify(userDao, never()).findById(eq(user.getId()));
        verify(menuDao, never()).findById(eq(menuItem.getId()));
        verify(userDao, never()).update(eq(user));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutDeleting_When_SessionNotFound() throws ServletException {
        MenuItem menuItem = new MenuItem( "menuItem", 0.0, 0, "path");
        User user = new User();

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> favoritesServlet.doPost(request, response));

        verify(userDao, never()).findById(eq(user.getId()));
        verify(menuDao, never()).findById(eq(menuItem.getId()));
        verify(userDao, never()).update(eq(user));
    }
}
