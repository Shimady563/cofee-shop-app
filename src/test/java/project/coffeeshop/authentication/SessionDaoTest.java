package project.coffeeshop.authentication;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SessionDaoTest {
    private static EntityManager entityManager;
    private final SessionDao sessionDao = new SessionDao();
    private final UserDao userDao = new UserDao();
    private static User user;

    @BeforeAll
    public static void setUpEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coffeeshopTestPersistenceUnit");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field entityManagerSessionDaoField = sessionDao.getClass().getSuperclass().getDeclaredField("entityManager");
        entityManagerSessionDaoField.setAccessible(true);
        entityManagerSessionDaoField.set(sessionDao, entityManager);

        Field entityManagerUserDaoField = userDao.getClass().getSuperclass().getDeclaredField("entityManager");
        entityManagerUserDaoField.setAccessible(true);
        entityManagerUserDaoField.set(userDao, entityManager);

        user = new User("u", "p");
        userDao.save(user);
    }

    @Test
    public void testSave() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId, LocalDateTime.now(), user);
        sessionDao.save(session);

        assertTrue(sessionDao.findById(sessionId).isPresent());
    }

    @Test
    public void testFindById() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId, LocalDateTime.now(), user);
        sessionDao.save(session);

        Optional<Session> sessionOptional = sessionDao.findById(sessionId);

        assertTrue(sessionOptional.isPresent());
        assertEquals(sessionOptional.get().getId(), sessionId);
    }

    @Test
    public void testDelete() {
        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId, LocalDateTime.now(), user);
        sessionDao.save(session);

        sessionDao.delete(session);

        assertTrue(sessionDao.findById(sessionId).isEmpty());
    }

    @Test
    public void testDeleteExpired() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();
        Session session1 = new Session(id1, LocalDateTime.now(), user);
        Session session2 = new Session(id1, LocalDateTime.now(), user);
        sessionDao.save(session1);
        sessionDao.save(session2);

        sessionDao.deleteExpiredSessions(LocalDateTime.now());

        assertTrue(sessionDao.findById(id1).isEmpty());
        assertTrue(sessionDao.findById(id2).isEmpty());
    }
}
