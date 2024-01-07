package project.coffeeshop.authentication;

import com.lambdaworks.crypto.SCryptUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
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
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfileServletTest {
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

    private ProfileServlet profileServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        profileServlet = new ProfileServlet();

        Field sessionDaoField = profileServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(profileServlet, sessionDao);

        Field userDaoField = profileServlet.getClass().getDeclaredField("userDao");
        userDaoField.setAccessible(true);
        userDaoField.set(profileServlet, userDao);

        Field webContextField = profileServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(profileServlet, webContext);

        Field templateEngineField = profileServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(profileServlet, templateEngine);
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

        profileServlet.doGet(request, response);

        verify(webContext).setVariable(eq("username"), eq("Test"));
        verify(templateEngine, times(1)).process(eq("profile"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ProcessTemplateWithoutAttribute_When_SessionIsInvalid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MIN);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        profileServlet.doGet(request, response);

        verify(webContext, never()).setVariable(eq("username"), any());
        verify(templateEngine, times(1)).process(eq("profile"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ProcessTemplateWithoutAttribute_When_CookieNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        profileServlet.doGet(request, response);

        verify(webContext, never()).setVariable(eq("username"), any());
        verify(templateEngine, times(1)).process(eq("profile"), eq(webContext), any());
    }

    @Test
    public void doGet_Should_ProcessTemplateWithoutAttribute_When_SessionNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        profileServlet.doGet(request, response);

        verify(webContext, never()).setVariable(eq("username"), any());
        verify(templateEngine, times(1)).process(eq("profile"), eq(webContext), any());
    }

    @Test
    public void doPost_Should_ChangePasswordAndUsername_When_NewPasswordAndUsernameAreDifferentFromTheOldOnes() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldUsername", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("NewUsername");
        when(request.getParameter("newPassword")).thenReturn("NewPassword");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        assertEquals(user.getUsername(), "NewUsername");
        assertTrue(SCryptUtil.check("NewPassword", user.getPassword()));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Changes saved successfully"));
        verify(userDao, times(1)).update(any());
    }

    @Test
    public void doPost_Should_ChangePassword_When_NewPasswordIsDifferentFromTheOldOne() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldUsername", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("OldUsername");
        when(request.getParameter("newPassword")).thenReturn("NewPassword");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        assertEquals(user.getUsername(), "OldUsername");
        assertTrue(SCryptUtil.check("NewPassword", user.getPassword()));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Changes saved successfully"));
        verify(userDao, times(1)).update(any());
    }

    @Test
    public void doPost_Should_ChangeUsername_When_NewUsernameIsDifferentFromTheOldOneAndNewPasswordIsTheSameAsOldOne() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldUsername", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("NewUsername");
        when(request.getParameter("newPassword")).thenReturn("OldPassword");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        assertEquals(user.getUsername(), "NewUsername");
        assertTrue(SCryptUtil.check("OldPassword", user.getPassword()));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Changes saved successfully"));
        verify(userDao, times(1)).update(any());
    }

    @Test
    public void doPost_Should_ChangeUsername_When_NewUsernameIsDifferentFromTheOldOneAndNewPasswordFieldIsBlank() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldPassword", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("NewUsername");
        when(request.getParameter("newPassword")).thenReturn("");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        assertEquals(user.getUsername(), "NewUsername");
        assertTrue(SCryptUtil.check("OldPassword", user.getPassword()));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Changes saved successfully"));
        verify(userDao, times(1)).update(any());
    }

    @Test
    public void doPost_Should_ReloadTemplateWithMessage_When_NewUsernameAndPasswordAreTheSameAsTheOldOnes() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldPassword", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("OldUsername");
        when(request.getParameter("newPassword")).thenReturn("OldPassword");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        assertEquals(user.getUsername(), "OldUsername");
        assertTrue(SCryptUtil.check("OldPassword", user.getPassword()));
        verify(webContext, times(1)).setVariable(eq("message"), eq("Username or password is the same"));
        verify(userDao, never()).update(any());
    }

    @Test
    public void doPost_Should_ReloadTemplateWithMessage_When_LengthOfNewUsernameIsLessThan3Symbols() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldPassword", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("1");
        when(request.getParameter("newPassword")).thenReturn("OldPassword");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Username or password is too short"));
        verify(userDao, never()).update(any());
    }

    @Test
    public void doPost_Should_ReloadTemplateWithMessage_When_LengthOfNewPasswordIsLessThan3Symbols() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldPassword", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("OldUsername");
        when(request.getParameter("newPassword")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Username or password is too short"));
        verify(userDao, never()).update(any());
    }

    @Test
    public void doPost_Should_ReloadTemplateWithMessage_When_LengthsOfNewPasswordAndNewUsernameAreLessThan3Symbols() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);
        User user = new User("OldUsername", SCryptUtil.scrypt("OldPassword", 16, 16, 16));

        when(request.getParameter("newLogin")).thenReturn("1");
        when(request.getParameter("newPassword")).thenReturn("1");
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));
        when(userDao.findById(any())).thenReturn(Optional.of(user));

        profileServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("message"), eq("Username or password is too short"));
        verify(userDao, never()).update(any());
    }
}
