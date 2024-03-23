package project.coffeeshop.menu.cart;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import project.coffeeshop.authentication.User;
import project.coffeeshop.authentication.UserDao;
import project.coffeeshop.menu.MenuDao;
import project.coffeeshop.menu.MenuItem;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CartDaoTest {
    private static EntityManager entityManager;
    private final CartDao cartDao = new CartDao();
    private static final UserDao userDao = new UserDao();
    private static final MenuDao menuDao = new MenuDao();

    private static final User user1 = new User("u1", "p1");
    private static final MenuItem menuItem1 = new MenuItem("m1", 1.0, 1, "i1");
    private static final User user2 = new User("u2", "p2");
    private static final MenuItem menuItem2 = new MenuItem("m2", 2.0, 2, "i2");

    @BeforeAll
    public static void setUpEntityManager() throws NoSuchFieldException, ServletException, IllegalAccessException {
        entityManager = Persistence.createEntityManagerFactory("coffeeshopTestPersistenceUnit").createEntityManager();

        Field userDaoField = userDao.getClass().getSuperclass().getDeclaredField("entityManager");
        userDaoField.setAccessible(true);
        userDaoField.set(userDao, entityManager);

        Field menuDaoField = menuDao.getClass().getSuperclass().getDeclaredField("entityManager");
        menuDaoField.setAccessible(true);
        menuDaoField.set(menuDao, entityManager);

        userDao.save(user1);
        userDao.save(user2);
        menuDao.save(menuItem1);
        menuDao.save(menuItem2);
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field cartDaoField = cartDao.getClass().getSuperclass().getDeclaredField("entityManager");
        cartDaoField.setAccessible(true);
        cartDaoField.set(cartDao, entityManager);
    }

    @AfterEach
    public void cleanUp() {
        cartDao.deleteByUser(user1);
        cartDao.deleteByUser(user2);
    }

    @Test
    public void testSave() throws ServletException {
        UserCart userCart1 = new UserCart(user1, menuItem1);
        UserCart userCart2 = new UserCart(user1, menuItem2);
        cartDao.save(userCart1);
        cartDao.save(userCart2);

        List<UserCart> cartItems = cartDao.findByUser(user1);

        assertEquals(2, cartItems.size());
    }

    @Test
    public void testUpdate() throws ServletException {
        UserCart userCart = new UserCart(user1, menuItem1);
        cartDao.save(userCart);

        userCart.setQuantity(2);
        cartDao.update(userCart);

        List<UserCart> cartItems = cartDao.findByUser(user1);

        assertEquals(1, cartItems.size());
        assertEquals(2, cartItems.get(0).getQuantity());
    }

    @Test
    public void testDeleteByUser() throws ServletException {
        UserCart userCart1 = new UserCart(user1, menuItem1);
        UserCart userCart2 = new UserCart(user1, menuItem2);
        UserCart userCart3 = new UserCart(user2, menuItem1);
        cartDao.save(userCart1);
        cartDao.save(userCart2);
        cartDao.save(userCart3);

        cartDao.deleteByUser(user1);

        List<UserCart> cartItems = cartDao.findByUser(user1);

        assertEquals(0, cartItems.size());

        List<UserCart> cartItems2 = cartDao.findByUser(user2);

        assertEquals(1, cartItems2.size());
    }

    @Test
    public void testDeleteByUserAndItem() throws ServletException {
        UserCart userCart = new UserCart(user1, menuItem1);
        cartDao.save(userCart);

        cartDao.deleteByUserAndItem(user1, menuItem1);

        List<UserCart> cartItems = cartDao.findByUser(user1);

        assertEquals(0, cartItems.size());
    }
}
