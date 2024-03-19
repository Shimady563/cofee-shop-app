package project.coffeeshop.news;

import jakarta.persistence.EntityTransaction;
import project.coffeeshop.commons.AbstractDao;

import java.util.List;
import java.util.Optional;

public class NewsDao extends AbstractDao<PieceOfNews, Long> {

    @Override
    public Optional<PieceOfNews> findById(Long id) {
        return Optional.ofNullable(entityManager.find(PieceOfNews.class, id));
    }

    public List<PieceOfNews> findAll() {
        return entityManager
                .createQuery("select p from PieceOfNews p", PieceOfNews.class)
                .getResultList();
    }

    public List<PieceOfNews> findByTitle(String title) {
        return entityManager
                .createQuery("select p from PieceOfNews p " +
                        "where p.title ilike :title", PieceOfNews.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }
}
