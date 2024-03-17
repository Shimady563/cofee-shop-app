package project.coffeeshop.commons;

import jakarta.persistence.EntityManager;

import java.util.Optional;

public abstract class AbstractDao<T, ID> {
    protected final EntityManager entityManager;

    protected AbstractDao() {
        entityManager = HibernateUtil.getEntityManager();
    }

    public abstract Optional<T> findById(ID id);


    public void save(T entity) {
        entityManager.persist(entity);
    }


    public void delete(T entity) {
        entityManager.remove(entity);
    }
}
