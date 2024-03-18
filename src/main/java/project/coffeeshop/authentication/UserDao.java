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

//public class UserDao {
//    private final DataSource dataSource;
//
//    public UserDao() throws ServletException {
//        try {
//            Context context = new InitialContext();
//            Context envContext = (Context) context.lookup("java:comp/env");
//            this.dataSource = (DataSource) envContext.lookup("jdbc/db");
//        } catch (NamingException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public long save(User user) throws ServletException {
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection
//                     .prepareStatement("insert into public.\"user\" (username, password) values (?, ?)",
//                             PreparedStatement.RETURN_GENERATED_KEYS)) {
//
//            preparedStatement.setString(1, user.getUsername());
//            preparedStatement.setString(2, user.getPassword());
//            preparedStatement.executeUpdate();
//
//            long generatedId = -1;
//            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
//            if (generatedKeys.next()) {
//               generatedId = generatedKeys.getLong(1);
//            }
//
//            return generatedId;
//
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public Optional<User> findByUsername(String username) throws ServletException {
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection
//                     .prepareStatement("select * from public.\"user\" where username = ?")) {
//
//            preparedStatement.setString(1, username);
//            preparedStatement.executeQuery();
//
//            return getUser(preparedStatement.getResultSet());
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public Optional<User> findById(Long id) throws ServletException {
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection
//                     .prepareStatement("select * from public.\"user\" where id = ?")) {
//
//            preparedStatement.setLong(1, id);
//            preparedStatement.executeQuery();
//
//            return getUser(preparedStatement.getResultSet());
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public void update(User user) throws ServletException {
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection
//                     .prepareStatement("update public.\"user\" " +
//                             "set username = ?, password = ? " +
//                             "where id = ?")) {
//
//            preparedStatement.setString(1, user.getUsername());
//            preparedStatement.setString(2, user.getPassword());
//            preparedStatement.setLong(3, user.getId());
//            preparedStatement.executeUpdate();
//
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    private Optional<User> getUser(ResultSet resultSet) throws SQLException {
//        User user = null;
//        while (resultSet.next()) {
//            long userId = resultSet.getLong(1);
//            String username = resultSet.getString(2);
//            String password = resultSet.getString(3);
//            user = new User(userId, username, password);
//        }
//
//        resultSet.close();
//
//        return Optional.ofNullable(user);
//    }
//}
