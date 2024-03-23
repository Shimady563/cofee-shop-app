package project.coffeeshop.news;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@WebServlet(name = "PieceOfNewsServlet", value = "/news/*")
public class PieceOfNewsServlet extends CoffeeShopServlet {
    public final NewsDao newsDao = new NewsDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        long pieceOfNewsId = Long.parseLong(request.getParameter("id"));
        Optional<PieceOfNews> pieceOfNewsOptional = newsDao.findById(pieceOfNewsId);

        pieceOfNewsOptional.ifPresent(pieceOfNews -> {
            webContext.setVariable("pieceOfNews", pieceOfNews);
            webContext.setVariable("formattedDate", pieceOfNews
                    .getCreationDate()
                    .format(DateTimeFormatter
                            .ofPattern("HH:mm, d MMMM yyyy")));
        });

        templateEngine.process("piece-of-news", webContext, response.getWriter());
    }
}
