package project.coffeeshop.commons;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthorizationFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Mock
    private SessionDao sessionDao;

    private AuthorizationFilter authorizationFilter;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        authorizationFilter = new AuthorizationFilter();

        Field sessionDaoField = authorizationFilter.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(authorizationFilter, sessionDao);
    }


    @Test
    public void doFilter_Should_ContinueFilterChain_When_SessionIsValid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        authorizationFilter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void doFilter_Should_RedirectToSignIn_When_SessionIsInvalid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MIN);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        try {
            authorizationFilter.doFilter(request, response, filterChain);
        } catch (MissingResourceException ignored) {
        }

        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/sign-in");
    }

    @Test
    public void doFilter_Should_RedirectToSignIn_When_SessionNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        try {
            authorizationFilter.doFilter(request, response, filterChain);
        } catch (MissingResourceException ignored) {
        }

        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/sign-in");
    }

    @Test
    public void doFilter_Should_RedirectToSignIn_When_CookieNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        try {
            authorizationFilter.doFilter(request, response, filterChain);
        } catch (MissingResourceException ignored) {
        }

        verify(sessionDao, never()).findById(any());
        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/sign-in");
    }
}
