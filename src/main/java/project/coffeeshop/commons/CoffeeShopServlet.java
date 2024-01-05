package project.coffeeshop.commons;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;
import project.coffeeshop.authentication.Session;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static project.coffeeshop.commons.ServletUtil.parsePath;


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
        webContext = ThymeleafConfig.getWebContext(request, response, getServletContext());

        try {
            super.service(request, response);
        } catch (ServletException e) {
            System.err.println(e.getMessage());
            getServletContext().setAttribute("path", parsePath(request.getHeader("referer")));
            response.sendRedirect(request.getContextPath() + "/error");
        }
    }
}