package project.coffeeshop.menu;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "MenuServlet", value = "/menu")
public class MenuServlet extends CoffeeShopServlet {
    private MenuDao menuDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        menuDao = new MenuDao();
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<MenuItem> menuItems = menuDao.findAll();
        if (menuItems.isEmpty()) {
            throw new ServletException("Menu items list is empty");
        }

        webContext.setVariable("menuItems", menuItems);
        templateEngine.process("menu", webContext, response.getWriter());
    }
}
