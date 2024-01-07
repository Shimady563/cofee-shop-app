package project.coffeeshop.authentication;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SignOutServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private SessionDao sessionDao;

    private SignOutServlet signOutServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        signOutServlet = new SignOutServlet();

        Field sessionDaoField = signOutServlet.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(signOutServlet, sessionDao);
    }

    @Test
    public void doGet_Should_DeleteSessionAndRedirectToHome() throws ServletException, IOException {
        UUID sessionId = UUID.randomUUID();
        Cookie sessionCookie = new Cookie("sessionId", sessionId.toString());

        when(request.getCookies()).thenReturn(new Cookie[]{sessionCookie});

        signOutServlet.doGet(request, response);

        assertEquals(sessionCookie.getMaxAge(), 0);
        verify(sessionDao, times(1)).delete(eq(sessionId));
        verify(response, times(1)).addCookie(eq(sessionCookie));
        verify(response, times(1)).sendRedirect(request.getContextPath() + "/home");
    }
}
