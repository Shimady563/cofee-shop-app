package project.coffeeshop.menu.cart;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.coffeeshop.authentication.User;
import project.coffeeshop.authentication.UserDao;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderDaoTest {
    private static EntityManager entityManager;
    private final OrderDao orderDao = new OrderDao();
    private static final UserDao userDao = new UserDao();
    private static final User user1 = new User("u1", "p1");
    private static final User user2 = new User("u2", "p2");


    @BeforeAll
    public static void setUpEntityManager() throws NoSuchFieldException, ServletException, IllegalAccessException {
        entityManager = Persistence.createEntityManagerFactory("coffeeshopTestPersistenceUnit").createEntityManager();

        Field userDaoField = userDao.getClass().getSuperclass().getDeclaredField("entityManager");
        userDaoField.setAccessible(true);
        userDaoField.set(userDao, entityManager);

        userDao.save(user1);
        userDao.save(user2);
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field orderDaoField = orderDao.getClass().getSuperclass().getDeclaredField("entityManager");
        orderDaoField.setAccessible(true);
        orderDaoField.set(orderDao, entityManager);
    }

    @Test
    public void testSave() throws ServletException {
        Order order = new Order(LocalDateTime.now(), LocalDateTime.now(), 1.0, user1);
        orderDao.save(order);

        Optional<Order> optionalOrder = orderDao.findById(order.getId());

        assertTrue(optionalOrder.isPresent());

        orderDao.delete(order);
    }

    @Test
    public void testFindById() throws ServletException {
        Order order = new Order(LocalDateTime.now(), LocalDateTime.now(), 1.0, user1);
        orderDao.save(order);

        Optional<Order> optionalOrder = orderDao.findById(order.getId());

        assertTrue(optionalOrder.isPresent());
        assertEquals(order.getId(), optionalOrder.get().getId());

        orderDao.delete(order);
    }

    @Test
    public void testFindByUser() throws ServletException {
        Order order1 = new Order(LocalDateTime.now(), LocalDateTime.now(), 1.0, user1);
        Order order2 = new Order(LocalDateTime.now(), LocalDateTime.now(), 2.0, user1);
        Order order3 = new Order(LocalDateTime.now(), LocalDateTime.now(), 3.0, user2);

        orderDao.save(order1);
        orderDao.save(order2);
        orderDao.save(order3);

        assertEquals(2, orderDao.findByUser(user1).size());
        assertEquals(1, orderDao.findByUser(user2).size());
    }
}
