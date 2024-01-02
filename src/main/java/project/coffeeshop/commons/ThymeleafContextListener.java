package project.coffeeshop.commons;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


import static project.coffeeshop.commons.ThymeleafConfig.getTemplateEngine;

@WebListener
public class ThymeleafContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        servletContext.setAttribute("templateEngine", getTemplateEngine(servletContext));
    }
}
