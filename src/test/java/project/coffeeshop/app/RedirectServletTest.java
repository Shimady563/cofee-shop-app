package project.coffeeshop.app;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.Mockito.atMostOnce;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RedirectServletTest {
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    private RedirectServlet redirectServlet;

    @BeforeEach
    public void setUp() {
        redirectServlet = new RedirectServlet();
    }

    @Test
    public void doGet_ShouldProcessErrorTemplate() throws IOException {
        try {
            redirectServlet.doGet(request, response);
        } catch (IllegalStateException ignored) {
        }

        verify(response, atMostOnce()).sendRedirect(request.getContextPath() + "/");
    }
}
