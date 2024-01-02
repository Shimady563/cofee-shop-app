package project.coffeeshop.commons;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@WebFilter(filterName = "AuthorizationFilter", servletNames = {"ProfileServlet"})
public class AuthorizationFilter implements Filter {
    private final SessionDao sessionDao = new SessionDao();

    public AuthorizationFilter() throws ServletException {
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
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
            } catch (ServletException e) {
                request.setAttribute("path", CoffeeShopServlet.parsePath(request.getHeader("referer")));
                response.sendRedirect(request.getContextPath() + "/error");
            }
        }

        request.setAttribute("path", CoffeeShopServlet.parsePath(request.getRequestURI()));
        response.sendRedirect(request.getContextPath() + "/sign-in");
     }
}
