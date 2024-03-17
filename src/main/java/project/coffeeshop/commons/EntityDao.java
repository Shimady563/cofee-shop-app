package project.coffeeshop.commons;

import java.util.Optional;

public interface EntityDao<T, ID> {
    Optional<T> findById(ID id);

    void save(T entity);

    void delete(T entity);
}
