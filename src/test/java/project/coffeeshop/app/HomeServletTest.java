package project.coffeeshop.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.authentication.User;
import project.coffeeshop.authentication.UserDao;

import java.io.IOException;
import java.lang.reflect.Field;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private WebContext webContext;
    @Mock
    private ITemplateEngine templateEngine;
    @Mock
    private SessionDao sessionDao;
    @Mock
    private UserDao userDao;

    private HomeServlet homeServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        homeServlet = new HomeServlet();

        Field sessionDaoField = homeServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(homeServlet, sessionDao);

        Field userDaoField = homeServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(homeServlet, userDao);

        Field webContextField = homeServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(homeServlet, webContext);

        Field templateEngineField = homeServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(homeServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute_When_SessionIsValid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User();
        user.setUsername("Test");

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        homeServlet.doGet(request, response);

        verify(webContext).setVariable(eq("auth"), eq(true));
        verify(webContext).setVariable(eq("username"), eq("Test"));
        verify(templateEngine).process(eq("index"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ProcessTemplateWithoutAttribute_When_SessionIsInvalid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MIN);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        homeServlet.doGet(request, response);

        verify(webContext, never()).setVariable(eq("auth"), eq(true));
        verify(webContext, never()).setVariable(eq("username"), any());
        verify(templateEngine).process(eq("index"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ProcessTemplateWithoutAttribute_When_CookieNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        homeServlet.doGet(request, response);

        verify(webContext, never()).setVariable(eq("auth"), eq(true));
        verify(webContext, never()).setVariable(eq("username"), any());
        verify(templateEngine).process(eq("index"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ProcessTemplateWithoutAttribute_When_SessionNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        homeServlet.doGet(request, response);

        verify(webContext, never()).setVariable(eq("auth"), eq(true));
        verify(webContext, never()).setVariable(eq("username"), any());
        verify(templateEngine).process(eq("index"), eq(webContext), any());
    }
}
