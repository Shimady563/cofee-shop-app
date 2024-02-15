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

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

        Field webContextField = favoritesServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(favoritesServlet, webContext);

        Field templateEngineField = favoritesServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(favoritesServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute_When_EverythingIsFine() throws ServletException, IOException {
        List<MenuItem> menuItems = List.of();
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findByUserId(eq(session.getUserId()))).thenReturn(menuItems);

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
    public void doGet_Should_ThrowAnException_When_SessionNotFound() throws ServletException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        assertThrows(ServletException.class, () -> favoritesServlet.doGet(request, response));
    }

    @Test
    public void doPost_Should_DeleteItemFromFavoritesAndReloadThePage_When_EverythingIsFine() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItems = List.of(menuItem);
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findByUserId(eq(session.getUserId()))).thenReturn(menuItems);

        favoritesServlet.doPost(request, response);

        verify(menuDao, times(1)).deleteUserFavorites(eq(session.getUserId()), eq(1));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutDeleting_When_CookieNotFound() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItems = List.of(menuItem);
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{}).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findByUserId(any(Long.class))).thenReturn(menuItems);

        favoritesServlet.doPost(request, response);

        verify(menuDao, never()).deleteUserFavorites(any(Long.class), eq(1));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutDeleting_When_SessionNotFound() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItems = List.of(menuItem);
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty()).thenReturn(Optional.of(session));
        when(menuDao.findByUserId(any(Long.class))).thenReturn(menuItems);

        favoritesServlet.doPost(request, response);

        verify(menuDao, never()).deleteUserFavorites(any(Long.class), eq(1));
    }
}
