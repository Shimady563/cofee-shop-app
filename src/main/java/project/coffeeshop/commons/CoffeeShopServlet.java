package project.coffeeshop.commons;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.Thymeleaf;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;
import project.coffeeshop.authentication.Session;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static project.coffeeshop.commons.ThymeleafConfig.getWebContext;

public abstract class CoffeeShopServlet extends HttpServlet {
    protected ITemplateEngine templateEngine;
    protected WebContext webContext;

    @Override
    public void init(ServletConfig config) throws ServletException {
        templateEngine = (ITemplateEngine) config.getServletContext().getAttribute("templateEngine");
        super.init(config);
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
        webContext = getWebContext(request, response, getServletContext());

        try {
            super.service(request, response);
        } catch (ServletException e) {
            System.out.println(e.getMessage());
            request.setAttribute("path", CoffeeShopServlet.parsePath(request.getHeader("referer")));
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }

    public static Optional<Cookie> findCookieByName(Cookie[] cookies, String name) {
        return Arrays.stream(cookies)
                .filter(c -> c.getName().equals(name))
                .findFirst();
    }

    public static boolean isValidSession(Session session, LocalDateTime now) {
        return session.getExpirationTime().isAfter(now);
    }

    public static String parsePath(String fullPath) {
        return fullPath.substring(fullPath.lastIndexOf('/'));
    }
}