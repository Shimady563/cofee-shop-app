package project.coffeeshop.app;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;

@WebServlet(name = "HomeServlet", value = {"", "/home"})
public class HomeServlet extends CoffeeShopServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        getServletContext().getRequestDispatcher("/index.jsp").forward(request, response);
    }
}
