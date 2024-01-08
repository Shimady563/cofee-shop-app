package project.coffeeshop.authentication;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

@WebListener
public class SessionContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

        try {
            SessionDao sessionDao = new SessionDao();
            long interval = 6 * 60 * 60;

            service.scheduleWithFixedDelay(() -> sessionDao.deleteExpiredSessions(LocalDateTime.now()), interval, interval, TimeUnit.SECONDS);

        } catch (ServletException e) {
            throw new RuntimeException(e);
        } finally {
            service.shutdown();
        }
    }
}
