package project.coffeeshop.app;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;

@WebServlet(name = "ErrorServlet", value = "/error")
public class ErrorServlet extends CoffeeShopServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        templateEngine.process("error", webContext, response.getWriter());
    }
}
