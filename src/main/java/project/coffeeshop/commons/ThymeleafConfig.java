package project.coffeeshop.commons;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

public class ThymeleafConfig {

    public static TemplateEngine getTemplateEngine(ServletContext servletContext) {
        JakartaServletWebApplication application = JakartaServletWebApplication.buildApplication(servletContext);
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(getTemplateResolver(application));
        return templateEngine;
    }

    public static ITemplateResolver getTemplateResolver(JakartaServletWebApplication application) {
        WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("");
        return templateResolver;
    }

    public static WebContext getWebContext(HttpServletRequest request, HttpServletResponse response, ServletContext servletContext) {
        return new WebContext(JakartaServletWebApplication
                .buildApplication(servletContext)
                .buildExchange(request, response));
    }
}
