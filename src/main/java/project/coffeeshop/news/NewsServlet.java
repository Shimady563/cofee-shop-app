package project.coffeeshop.news;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "NewsServlet", value = "/news")
public class NewsServlet extends CoffeeShopServlet {
    private NewsDao newsDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        newsDao = new NewsDao();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<PieceOfNews> news = newsDao.findAll();

        webContext.setVariable("news", news);
        templateEngine.process("news", webContext, response.getWriter());
    }
}
