package project.coffeeshop.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
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
public class SignUpServletTest {
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

    private SignUpServlet signUpServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        signUpServlet = new SignUpServlet();

        Field sessionDaoField = signUpServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(signUpServlet, sessionDao);

        Field userDaoField = signUpServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(signUpServlet, userDao);

        Field webContextField = signUpServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(signUpServlet, webContext);

        Field templateEngineField = signUpServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(signUpServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessSignUp() throws ServletException, IOException {
        signUpServlet.doGet(request, response);

        verify(templateEngine).process(eq("sign-up"), eq(webContext), any());
    }

    @Test
    public void doPost_Should_SaveUserAndSession_If_RequestParametersAreCorrect() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("confirmPassword")).thenReturn("password");
        when(userDao.findByUsername(any())).thenReturn(Optional.empty());

        try {
            signUpServlet.doPost(request, response);
        } catch (IllegalStateException ignored) {
        }

        verify(sessionDao, times(1)).save(any());
        verify(userDao, times(1)).save(any());
        verify(response, times(1)).addCookie(any());
        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ReloadSignUpWithMessage_If_LengthOfPasswordIsLessThan3() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("1");
        when(request.getParameter("confirmPassword")).thenReturn("1");

        signUpServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Username or password is too short"));
        verify(templateEngine, times(1)).process(eq("sign-up"), eq(webContext), any());
        verify(response, never()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ReloadSignUpWithMessage_If_LengthOfUsernameIsLessThan3() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("1");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("confirmPassword")).thenReturn("password");

        signUpServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Username or password is too short"));
        verify(templateEngine, times(1)).process(eq("sign-up"), eq(webContext), any());
        verify(response, never()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ReloadSignUpWithMessage_If_UserAlreadyExists() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("confirmPassword")).thenReturn("password");
        when(userDao.findByUsername(any())).thenReturn(Optional.of(new User()));

        signUpServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("User already exists"));
        verify(templateEngine, times(1)).process(eq("sign-up"), eq(webContext), any());
        verify(response, never()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ReloadSignUpWithMessage_If_PasswordsDoNotMath() throws ServletException, IOException {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("confirmPassword")).thenReturn("otherPassword");
        when(userDao.findByUsername(any())).thenReturn(Optional.empty());

        signUpServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Password mismatch"));
        verify(templateEngine, times(1)).process(eq("sign-up"), eq(webContext), any());
        verify(response, never()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doPost_Should_ThrowServletException_When_UserSavingFails() throws ServletException {
        when(request.getParameter("username")).thenReturn("username");
        when(request.getParameter("password")).thenReturn("password");
        when(request.getParameter("confirmPassword")).thenReturn("password");
        when(userDao.findByUsername(any())).thenReturn(Optional.empty());
        when(userDao.save(any())).thenReturn(-1L);

        Assertions.assertThrows(ServletException.class, () -> signUpServlet.doPost(request, response));
    }
}
