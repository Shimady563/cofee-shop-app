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

        Field webContextField = menuServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(menuServlet, webContext);

        Field templateEngineField = menuServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(menuServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute_When_MenuItemListIsNotEmpty() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
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
    public void doPost_Should_SaveItemToFavoritesAndReloadThePage_When_EverythingIsFine() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItemList = List.of(menuItem);
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(menuDao.findAll()).thenReturn(menuItemList);

        menuServlet.doPost(request, response);

        verify(menuDao, times(1)).saveToFavorites(eq(session.getUserId()), eq(1));
        verify(webContext, times(1)).setVariable(eq("itemId"), eq(1));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Added to favorites"));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutSaving_When_CookieNotFound() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItemList = List.of(menuItem);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{});
        when(menuDao.findAll()).thenReturn(menuItemList);

        menuServlet.doPost(request, response);

        verify(menuDao, never()).saveToFavorites(any(Long.class), eq(1));
        verify(webContext, never()).setVariable(eq("itemId"), eq(1));
        verify(webContext, never()).setVariable(eq("message"), eq("Added to favorites"));
    }

    @Test
    public void doPost_Should_ReloadThePageWithoutSaving_When_SessionNotFound() throws ServletException, IOException {
        MenuItem menuItem = new MenuItem(1, "menuItem", 0.0, 0, "path");
        List<MenuItem> menuItemList = List.of(menuItem);

        when(request.getParameter("menuItemId")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());
        when(menuDao.findAll()).thenReturn(menuItemList);

        menuServlet.doPost(request, response);

        verify(menuDao, never()).saveToFavorites(any(Long.class), eq(1));
        verify(webContext, never()).setVariable(eq("itemId"), eq(1));
        verify(webContext, never()).setVariable(eq("message"), eq("Added to favorites"));
    }
}
