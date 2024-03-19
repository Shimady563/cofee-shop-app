package project.coffeeshop.authentication;

import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import project.coffeeshop.commons.AbstractDao;

import java.util.Optional;

public class UserDao extends AbstractDao<User, Long> {

    @Override
    public Optional<User> findById(Long userId) {
        return Optional.ofNullable(entityManager.find(User.class, userId));
    }

    public void update(User user) throws ServletException {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager.merge(user);
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }

            throw new ServletException(e);
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional
                    .of(entityManager
                            .createQuery("select u from User u " +
                                    "where username = :username", User.class)
                            .setParameter("username", username)
                            .getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
