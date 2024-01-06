package project.coffeeshop.app;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.lang.reflect.Field;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ErrorServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private WebContext webContext;
    @Mock
    private ITemplateEngine templateEngine;
    
    private ErrorServlet errorServlet;
    
    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        errorServlet = new ErrorServlet();

        Field webContextField = errorServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(errorServlet, webContext);

        Field templateEngineField = errorServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(errorServlet, templateEngine);
    }

    @Test
    public void doGet_ShouldProcessErrorTemplate() throws IOException {
        errorServlet.doGet(request, response);

        verify(templateEngine).process(eq("error"), eq(webContext), any());
    }
}
