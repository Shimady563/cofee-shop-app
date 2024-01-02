package project.coffeeshop.authentication;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;
import project.coffeeshop.app.HomeServlet;
import project.coffeeshop.commons.CoffeeShopServlet;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

@WebListener
public class SessionContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        long interval = 1000 * 6 * 60 * 60;
        try {
            SessionDao sessionDao = new SessionDao();
            Timer timer = new Timer("ExpiredSessionsDeletion");
            timer.schedule(new TimerTask() {
                @Override
                public void run()  {
                    try {
                        sessionDao.deleteExpiredSessions(LocalDateTime.now());
                    } catch (ServletException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, interval, interval);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
