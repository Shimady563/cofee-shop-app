package project.coffeeshop.commons;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.SessionDao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static project.coffeeshop.commons.ServletUtil.*;

@WebFilter(filterName = "AuthorizationFilter", servletNames = {"ProfileServlet", "MenuServlet", "FavoritesServlet", "NewsServlet", "PieceOfNewsServlet", "CartServlet"})
public class AuthorizationFilter extends HttpFilter {
    private SessionDao sessionDao;

    @Override
    public void init(FilterConfig config) throws ServletException {
        sessionDao = new SessionDao();
        super.init(config);
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        Cookie[] cookies = request.getCookies();
        Optional<Cookie> cookieOptional = findCookieByName(cookies, "sessionId");

        if (cookieOptional.isPresent()) {
            try {
                Optional<Session> sessionOptional = sessionDao.findById(UUID.fromString(cookieOptional.get().getValue()));

                if (sessionOptional.isPresent() && isValidSession(sessionOptional.get(), LocalDateTime.now())) {
                    filterChain.doFilter(request, response);
                    return;
                }
            } catch (ServletException e) {
                System.err.println(e.getMessage());
                getServletContext().setAttribute("path", parsePath(request.getHeader("referer")));
                response.sendRedirect(request.getContextPath() + "/error");
            }
        }

        getServletContext().setAttribute("path", parsePath(request.getRequestURI()));
        response.sendRedirect(request.getContextPath() + "/sign-in");
    }
}
