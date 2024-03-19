package project.coffeeshop.authentication;

import jakarta.persistence.EntityTransaction;
import project.coffeeshop.commons.AbstractDao;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionDao extends AbstractDao<Session, UUID> {

    public Optional<Session> findById(UUID id) {
        return Optional.ofNullable(entityManager.find(Session.class, id));
    }

    public void deleteExpiredSessions(LocalDateTime now) {
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();

            entityManager
                    .createQuery("delete from Session " +
                            "where expirationTime < :time")
                    .setParameter("time", now)
                    .executeUpdate();
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
}
