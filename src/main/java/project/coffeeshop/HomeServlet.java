package project.coffeeshop;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import project.coffeeshop.authentication.Session;
import project.coffeeshop.authentication.User;
import project.coffeeshop.commons.CoffeeShopServlet;
import project.coffeeshop.authentication.SessionDao;
import project.coffeeshop.authentication.UserDao;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@WebServlet(name = "HomeServlet", value = "")
public class HomeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.sendRedirect(req.getContextPath() + "index.jsp");
    }
}
