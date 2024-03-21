package project.coffeeshop.authentication;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserDaoTest {
    private static EntityManager entityManager;
    private final UserDao userDao = new UserDao();

    @BeforeAll
    public static void setUpEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coffeeshopTestPersistenceUnit");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @BeforeEach
    public void setUpUserDao() throws NoSuchFieldException, IllegalAccessException {
        Field entityManagerField = userDao.getClass().getSuperclass().getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(userDao, entityManager);
    }

    @Test
    public void testSave() throws ServletException {
        User user = new User("username", "pass");
        userDao.save(user);

        assertNotNull(user.getId());
    }

    @Test
    public void testFindById() throws ServletException {
        User user = new User("username1", "pass1");
        userDao.save(user);
        Long userId = user.getId();
        Optional<User> userOptional = userDao.findById(userId);

        assertTrue(userOptional.isPresent());
        assertEquals(userOptional.get().getId(), userId);

        Long nonExistingId = 1000L;

        assertTrue(userDao.findById(nonExistingId).isEmpty());
    }

    @Test
    public void testFindByUsername() throws ServletException {
        String username = "username2";
        User user = new User(username, "pass1");
        userDao.save(user);
        Optional<User> userOptional = userDao.findByUsername(username);

        assertTrue(userOptional.isPresent());
        assertEquals(userOptional.get().getUsername(), username);

        String nonExistingUsername = "notAUsername";

        assertTrue(userDao.findByUsername(nonExistingUsername).isEmpty());
    }

    @Test
    public void testDelete() throws ServletException {
        User user = new User("username3", "pass3");
        userDao.save(user);
        Long userId = user.getId();

        userDao.delete(user);

        assertTrue(userDao.findById(userId).isEmpty());
    }

    @Test
    public void testUpdate() throws ServletException {
        User user = new User("username3", "pass3");
        userDao.save(user);
        Long userId = user.getId();
        String newUsername = "newUsername";
        String newPass = "newPass";
        user.setUsername(newUsername);
        user.setPassword(newPass);

        userDao.update(user);

        Optional<User> userOptional = userDao.findById(userId);
        assertEquals(userOptional.get().getUsername(), newUsername);
        assertEquals(userOptional.get().getPassword(), newPass);
    }
}
