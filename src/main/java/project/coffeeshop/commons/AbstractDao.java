package project.coffeeshop.commons;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import project.coffeeshop.menu.cart.UserCart;

import java.util.Optional;

public abstract class AbstractDao<T, ID> {
    protected final EntityManager entityManager;

    protected AbstractDao() {
        entityManager = HibernateUtil.getEntityManager();
    }

    public abstract Optional<T> findById(ID id);

    public void save(T entity) throws ServletException {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager.persist(entity);
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServletException(e);
        }
    }

    public void delete(T entity) throws ServletException {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager.remove(entity);
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServletException(e);
        }
    }

    public void update(T entity) throws ServletException {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager.merge(entity);
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServletException(e);
        }
    }
}
