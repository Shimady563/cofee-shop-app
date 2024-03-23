package project.coffeeshop.menu;

import project.coffeeshop.commons.AbstractDao;

import java.util.List;
import java.util.Optional;

public class MenuDao extends AbstractDao<MenuItem, Long> {

    @Override
    public Optional<MenuItem> findById(Long id) {
        return Optional.ofNullable(entityManager.find(MenuItem.class, id));
    }

    public List<MenuItem> findAll() {
        return entityManager
                .createQuery("select m from MenuItem m", MenuItem.class)
                .getResultList();
    }
}
