package project.coffeeshop.authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationFilterTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;

    @Mock
    private SessionDao sessionDao;

    private AuthenticationFilter authenticationFilter;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        authenticationFilter = new AuthenticationFilter();

        Field sessionDaoField = authenticationFilter.getClass().getDeclaredField("sessionDao");
        sessionDaoField.setAccessible(true);
        sessionDaoField.set(authenticationFilter, sessionDao);
    }

    @Test
    public void doFilter_Should_RedirectToProfile_When_SessionIsValid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MAX);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        try {
            authenticationFilter.doFilter(request, response, filterChain);
        } catch (MissingResourceException ignored) {
        }

        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/profile");
    }

    @Test
    public void doFilter_Should_ContinueFilterChain_When_SessionIsInvalid() throws ServletException, IOException {
        Session session = new Session();
        session.setExpirationTime(LocalDateTime.MIN);

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.of(session));

        authenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_Should_ContinueFilterChain_When_SessionNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("sessionId", UUID.randomUUID().toString())});
        when(sessionDao.findById(any())).thenReturn(Optional.empty());

        authenticationFilter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    public void doFilter_Should_ContinueFilterChain_When_CookieNotFound() throws ServletException, IOException {
        when(request.getCookies()).thenReturn(new Cookie[]{});

        authenticationFilter.doFilter(request, response, filterChain);

        verify(sessionDao, never()).findById(any());
        verify(filterChain).doFilter(request, response);
    }
}
