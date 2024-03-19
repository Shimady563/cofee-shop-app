package project.coffeeshop.news;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewsDaoTest {
    private static EntityManager entityManager;
    private final NewsDao newsDao = new NewsDao();


    @BeforeAll
    public static void setUpEntityManager() {
        EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("coffeeshopTestPersistenceUnit");
        entityManager = entityManagerFactory.createEntityManager();
    }

    @BeforeEach
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field entityManagerField = newsDao.getClass().getSuperclass().getDeclaredField("entityManager");
        entityManagerField.setAccessible(true);
        entityManagerField.set(newsDao, entityManager);

        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("insert into public.news " +
                "(id, creation_date, title, image, article)" +
                "values (1, now(), 'title', 'image', 'article'), " +
                        "(2, now(), 'tatle', 'image1', 'article')")
                .executeUpdate();
        entityManager.getTransaction().commit();
    }

    @Test
    public void testFindById() {
        Optional<PieceOfNews> piece = newsDao.findById(1L);
        assertTrue(piece.isPresent());
        assertEquals(Long.valueOf(1L), piece.get().getId());
    }

    @Test
    public void testFindAll() {
        List<PieceOfNews> pieces = newsDao.findAll();
        assertEquals(pieces.size(), 2);
    }

    @Test
    public void testFindByTitle() {
        List<PieceOfNews> pieces = newsDao.findByTitle("it");
        assertEquals(pieces.size(), 1);
        assertEquals(pieces.get(0).getTitle(), "title");
    }

    @AfterEach
    public void cleanUp() {
        entityManager.getTransaction().begin();
        entityManager.createNativeQuery("delete from public.news")
                .executeUpdate();
        entityManager.getTransaction().commit();
    }
}
