package project.coffeeshop.authentication;

import jakarta.servlet.ServletException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class SessionDao {
    private final DataSource dataSource;

    public SessionDao() throws ServletException {
        try {
            Context context = new InitialContext();
            Context envContext = (Context) context.lookup("java:comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/db");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    public void save(Session session) throws ServletException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("insert into public.session (id, user_id, expiration_time) values (?, ?, ?)")) {

            preparedStatement.setObject(1, session.getId());
            preparedStatement.setLong(2, session.getUserId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(session.getExpirationTime()));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public Optional<Session> findById(UUID sessionId) throws ServletException {
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection
                .prepareStatement("select id, user_id, expiration_time from public.session where id = ?")) {

            preparedStatement.setObject(1, sessionId);

            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            Session session = null;
            while (resultSet.next()) {
                UUID id = (UUID) resultSet.getObject(1);
                long userId = resultSet.getLong(2);
                LocalDateTime expirationTime = resultSet.getTimestamp(3).toLocalDateTime();
                session = new Session(id, userId, expirationTime);
            }

            return Optional.ofNullable(session);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public void deleteExpiredSessions(LocalDateTime now) throws ServletException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("delete from public.session where expiration_time < ?")) {

            preparedStatement.setTimestamp(1, Timestamp.valueOf(now));
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
