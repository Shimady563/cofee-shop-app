package project.coffeeshop.authentication;

import com.lambdaworks.crypto.SCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SignInServletTest {
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

    private SignInServlet signInServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        signInServlet = new SignInServlet();

        Field sessionDaoField = signInServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(signInServlet, sessionDao);

        Field userDaoField = signInServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(signInServlet, userDao);

        Field webContextField = signInServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(signInServlet, webContext);

        Field templateEngineField = signInServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(signInServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessSignUp() throws ServletException, IOException {
        signInServlet.doGet(request, response);

        verify(templateEngine).process(eq("sign-in"), eq(webContext), any());
    }

    @Test
    public void doPost_Should_SaveSession_If_RequestParametersAreCorrect() throws ServletException, IOException {
        User user = new User("username", SCryptUtil.scrypt("password", 16, 16, 16));

        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(userDao.findByUsername(any())).thenReturn(Optional.of(user));

        try {
            signInServlet.doPost(request, response);
        } catch (IllegalStateException ignored) {
        }

        verify(sessionDao, times(1)).save(any());
        verify(response, times(1)).addCookie(any());
        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ReloadSignInWithMessage_If_UserNotFound() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(userDao.findByUsername(any())).thenReturn(Optional.empty());

        signInServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("User not found"));
        verify(templateEngine, times(1)).process(eq("sign-in"), eq(webContext), any());
        verify(response, never()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ReloadSignInWithMessage_If_PasswordsDoNotMatch() throws ServletException, IOException {
        User user = new User("username", SCryptUtil.scrypt("password", 16, 16, 16));

        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("otherPassword");
        when(userDao.findByUsername(any())).thenReturn(Optional.of(user));

        signInServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Wrong password"));
        verify(templateEngine, times(1)).process(eq("sign-in"), eq(webContext), any());
        verify(response, never()).sendRedirect(request.getContextPath() + "/profile");
    }
}