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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PieceNewsServletTest {
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

    private PieceOfNewsServlet pieceOfNewsServlet;

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        pieceOfNewsServlet = new PieceOfNewsServlet();

        Field newsDaoField = pieceOfNewsServlet.getClass().getField("newsDao");
        newsDaoField.setAccessible(true);
        newsDaoField.set(pieceOfNewsServlet, newsDao);

        Field webContextField = pieceOfNewsServlet.getClass().getSuperclass().getDeclaredField("webContext");
        webContextField.setAccessible(true);
        webContextField.set(pieceOfNewsServlet, webContext);

        Field templateEngineField = pieceOfNewsServlet.getClass().getSuperclass().getDeclaredField("templateEngine");
        templateEngineField.setAccessible(true);
        templateEngineField.set(pieceOfNewsServlet, templateEngine);
    }

    @Test
    public void doGet_Should_ProcessTemplateWithAttribute() throws ServletException, IOException, NoSuchFieldException, IllegalAccessException {
        String id = "1";
        PieceOfNews pieceOfNews = new PieceOfNews();

        Field date = pieceOfNews.getClass().getDeclaredField("creationDate");
        date.setAccessible(true);
        date.set(pieceOfNews, LocalDateTime.now());

        String formattedDate = pieceOfNews
                .getCreationDate()
                .format(DateTimeFormatter
                        .ofPattern("HH:mm, d MMMM yyyy"));

        when(request.getParameter("id")).thenReturn(id);
        when(newsDao.findById(eq(Long.parseLong(id)))).thenReturn(Optional.of(pieceOfNews));

        pieceOfNewsServlet.doGet(request, response);

        verify(webContext, times(1)).setVariable(eq("pieceOfNews"), eq(pieceOfNews));
        verify(webContext, times(1)).setVariable(eq("formattedDate"), eq(formattedDate));
        verify(templateEngine, times(1)).process(eq("piece-of-news"), eq(webContext), any());
    }
}
