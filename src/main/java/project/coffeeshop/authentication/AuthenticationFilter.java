package project.coffeeshop.authentication;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@WebFilter(filterName = "AuthFilter", servletNames = {"SignIn"})
public class AuthenticationFilter implements Filter {
    private final SessionDao sessionDao = new SessionDao();

    public AuthenticationFilter() throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = CoffeeShopServlet.findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            try {
                Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

                if (sessionOptional.isPresent() && CoffeeShopServlet.isValidSession(sessionOptional.get(), LocalDateTime.now())) {
                    Optional<String> path = Optional.ofNullable((String) request.getAttribute("path"));
                    response.sendRedirect(request.getContextPath() + (path.orElse("/profile")));
                    return;
                }
            } catch (ServletException e) {
                response.sendRedirect(request.getContextPath() + "/error");
            }
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
