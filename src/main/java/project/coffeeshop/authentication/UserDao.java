package project.coffeeshop.authentication;

import jakarta.servlet.ServletException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserDao {
    private final DataSource dataSource;

    public UserDao() throws ServletException {
        try {
            Context context = new InitialContext();
            Context envContext = (Context) context.lookup("java:comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/db");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    public long save(User user) throws ServletException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("insert into public.\"user\" (username, password) values (?, ?)",
                             PreparedStatement.RETURN_GENERATED_KEYS)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.executeUpdate();

            long generatedId = -1;
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
               generatedId = generatedKeys.getLong(1);
            }

            return generatedId;

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public Optional<User> findByUsername(String username) throws ServletException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement("select * from public.\"user\" where username = ?")) {

            preparedStatement.setString(1, username);
            preparedStatement.executeQuery();
            ResultSet resultSet = preparedStatement.getResultSet();

            User user = null;
            while (resultSet.next()) {
                long id = resultSet.getLong(1);
                String userName = resultSet.getString(2);
                String password = resultSet.getString(3);
                user = new User(id, userName, password);
            }

            resultSet.close();

            return Optional.ofNullable(user);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
