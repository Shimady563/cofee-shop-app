package project.coffeeshop.menu.cart;

import jakarta.persistence.EntityTransaction;
import jakarta.servlet.ServletException;
import project.coffeeshop.authentication.User;
import project.coffeeshop.commons.AbstractDao;
import project.coffeeshop.menu.MenuItem;

import java.util.List;
import java.util.Optional;

public class CartDao extends AbstractDao<UserCart, Long> {

    @Override
    public Optional<UserCart> findById(Long id) {
        return Optional.ofNullable(entityManager.find(UserCart.class, id));
    }

    public List<UserCart> findByUser(User user) {
        return entityManager
                .createQuery("select uc from UserCart uc " +
                        "where user = :user " +
                        "order by menuItem.name, menuItem.volume", UserCart.class)
                .setParameter("user", user)
                .getResultList();
    }

    public void deleteByUser(User user) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager
                    .createQuery("delete from UserCart " +
                            "where user = :user")
                    .setParameter("user", user)
                    .executeUpdate();
            entityManager.clear();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }

    public void deleteByUserAndItem(User user, MenuItem menuItem) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager
                    .createQuery("delete from UserCart " +
                            "where user = :user and menuItem = :menuItem")
                    .setParameter("user", user)
                    .setParameter("menuItem", menuItem)
                    .executeUpdate();
            entityManager.clear();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
}
