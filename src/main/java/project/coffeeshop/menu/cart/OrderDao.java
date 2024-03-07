package project.coffeeshop.menu.cart;

import jakarta.servlet.ServletException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDao {
    private final DataSource dataSource;

    public OrderDao() throws ServletException {
        try {
            Context context = new InitialContext();
            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/db");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    public List<Order> findAll(long userId) throws ServletException {
        String query = "select a.* from public.\"order\" a " +
                "join public.user_order b on a.id = b.order_id " +
                "where b.user_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(query)) {

            preparedStatement.setLong(1, userId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Order> orders = new ArrayList<>();
            while (resultSet.next()) {
                Order order = new Order(
                        resultSet.getLong("id"),
                        resultSet.getTimestamp("creation_time").toLocalDateTime(),
                        resultSet.getTimestamp("ready_time").toLocalDateTime(),
                        resultSet.getDouble("price")
                );
                orders.add(order);
            }

            return orders;
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public void save(long userId, Order order) throws ServletException {
        String saveOrder = "insert into public.\"order\" " +
                "(creation_time, ready_time, price) " +
                "values (?, ?, ?)";
        String saveUserOrder = "insert into public.user_order (user_id, order_id) " +
                "values (?, ?)";
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement saveOrderStatement = connection
                    .prepareStatement(saveOrder, PreparedStatement.RETURN_GENERATED_KEYS);
                 PreparedStatement saveUserOrderStatement = connection
                         .prepareStatement(saveUserOrder)) {

                saveOrderStatement.setTimestamp(1, Timestamp.valueOf(order.getCreationTime()));
                saveOrderStatement.setTimestamp(2, Timestamp.valueOf(order.getReadyTime()));
                saveOrderStatement.setDouble(3, order.getPrice());
                saveOrderStatement.executeUpdate();

                long orderId = -1;
                ResultSet generatedKeys = saveOrderStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    orderId = generatedKeys.getLong(1);
                }

                if (orderId == -1) {
                    throw new SQLException("Couldn't find generated id for new order of user " + userId);
                }

                saveUserOrderStatement.setLong(1, userId);
                saveUserOrderStatement.setLong(2, orderId);
                saveUserOrderStatement.executeUpdate();
                connection.commit();
            } catch (SQLException e) {
                throw new ServletException(e);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}