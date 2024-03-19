package project.coffeeshop.authentication;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.time.LocalDateTime;
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
        } finally {
            service.shutdown();
        }
    }
}
