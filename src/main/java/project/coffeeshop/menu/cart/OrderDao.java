package project.coffeeshop.menu.cart;

import project.coffeeshop.authentication.User;
import project.coffeeshop.commons.AbstractDao;

import java.util.List;
import java.util.Optional;

public class OrderDao extends AbstractDao<Order, Long> {

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Order.class, id));
    }

    public List<Order> findByUser(User user) {
        return entityManager
                .createQuery("select o from Order o where " +
                        "user = :user", Order.class)
                .setParameter("user", user)
                .getResultList();
    }
}
