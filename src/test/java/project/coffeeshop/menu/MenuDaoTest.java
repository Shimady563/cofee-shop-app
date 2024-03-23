package project.coffeeshop.menu;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Persistence;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MenuDaoTest {
    private static EntityManager entityManager;
    private final MenuDao menuDao = new MenuDao();

    @BeforeAll
    public static void setUpEntityManager() {
        entityManager = Persistence.createEntityManagerFactory("coffeeshopTestPersistenceUnit").createEntityManager();
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field menuDaoField = menuDao.getClass().getSuperclass().getDeclaredField("entityManager");
        menuDaoField.setAccessible(true);
        menuDaoField.set(menuDao, entityManager);

        entityManager.getTransaction().begin();
        entityManager
                .createNativeQuery("insert into public.menu_item " +
                "(id, name, price, volume, image) " +
                "values (1000, 'menuItem1', 1.0, 1, 'path1'), " +
                "(2000, 'menuItem2', 2.0, 2, 'path2')")
                .executeUpdate();
        entityManager.getTransaction().commit();
    }

    @AfterEach
    public void clearUp() {
        entityManager.getTransaction().begin();
        entityManager
                .createNativeQuery("delete from public.menu_item")
                .executeUpdate();
        entityManager.getTransaction().commit();

    }

    @Test
    public void testFindById() {
        Long id = 1000L;

        Optional<MenuItem> menuItemOptional = menuDao.findById(id);

        assertTrue(menuItemOptional.isPresent());
        assertEquals(menuItemOptional.get().getId(), id);
    }

    @Test
    public void testFindAll() {
        List<MenuItem> menuItems = menuDao.findAll();

        assertEquals(menuItems.size(), 2);
    }

    @Test
    public void testSave() throws ServletException {
        MenuItem menuItem = new MenuItem( "m", 123, 12354, "p");
        menuDao.save(menuItem);
        Long id = menuItem.getId();
        System.out.println(id);

        Optional<MenuItem> menuItemOptional = menuDao.findById(id);

        assertTrue(menuItemOptional.isPresent());
    }
}
