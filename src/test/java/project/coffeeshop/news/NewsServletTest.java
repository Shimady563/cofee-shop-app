package project.coffeeshop.news;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NewsServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private WebContext webContext;
    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private NewsDao newsDao;

    private NewsServlet newsServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        newsServlet = new NewsServlet();

        Field newsDaoField = newsServlet.getClass().getDeclaredField("newsDao");
        newsDaoField.setAccessible(true);
        newsDaoField.set(newsServlet, newsDao);

        Field webContextField = newsServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(newsServlet, webContext);

        Field templateEngineField = newsServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(newsServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute() throws ServletException, IOException {
        List<PieceOfNews> news = List.of(new PieceOfNews());

        when(newsDao.findAll()).thenReturn(news);

        newsServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("news"), eq(news));
        verify(templateEngine, times(1)).process(eq("news"), eq(webContext), any());
    }

    @Test
    public void doPost_Should_SetAttributeAndReloadThePage() throws ServletException, IOException {
        String requestText = "request";
        List<PieceOfNews> news = List.of(new PieceOfNews());

        when(request.getParameter("requestText")).thenReturn(requestText);
        when(newsDao.findByTitle(eq(requestText))).thenReturn(news);

        newsServlet.doPost(request, response);

        verify(webContext, times(1)).setVariable(eq("news"), eq(news));
        verify(templateEngine, times(1)).process(eq("news"), eq(webContext), any());
    }
}
