package project.coffeeshop.app;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "RedirectServlet", value = "/redirect")
public class RedirectServlet extends CoffeeShopServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Optional<String> path = Optional.ofNullable((String) request.getAttribute("path"));
        response.sendRedirect(request.getContextPath() + path.orElse("/"));
    }
}
