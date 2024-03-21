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
            entityManager.flush();

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
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
}

//public class CartDao {
//    private final DataSource dataSource;
//
//    public CartDao() throws ServletException {
//        try {
//            Context context = new InitialContext();
//            dataSource = (DataSource) context.lookup("java:comp/env/jdbc/db");
//        } catch (NamingException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public List<CartItem> findAll(long userId) throws ServletException {
//        String query = "select b.*, a.quantity from public.user_cart a " +
//                "join public.menu_item b on a.menu_item_id = b.id " +
//                "where a.user_id = ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement =
//                     connection.prepareStatement(query)) {
//
//            preparedStatement.setLong(1, userId);
//            return retrieveCartItems(preparedStatement.executeQuery());
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public void saveToCart(long userId, long menuItemId) throws ServletException {
//        String query = "insert into public.user_cart " +
//                "(user_id, menu_item_id) " +
//                "values (?, ?)";
//        try (Connection connection = dataSource.getConnection()) {
//            if (!isAlreadyAdded(connection, userId, menuItemId)) {
//                try (PreparedStatement preparedStatement =
//                             connection.prepareStatement(query)) {
//
//                    preparedStatement.setLong(1, userId);
//                    preparedStatement.setLong(2, menuItemId);
//                    preparedStatement.executeUpdate();
//                }
//            }
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public void updateQuantity(long userId, long menuItemId, int newQuantity) throws ServletException {
//        String query = "update public.user_cart " +
//                "set quantity = ? " +
//                "where user_id = ? and menu_item_id = ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement =
//                     connection.prepareStatement(query)) {
//
//            preparedStatement.setInt(1, newQuantity);
//            preparedStatement.setLong(2, userId);
//            preparedStatement.setLong(3, menuItemId);
//            int rowsAffected = preparedStatement.executeUpdate();
//
//            if (rowsAffected == 0) {
//                throw new ServletException("Couldn't find menu item " +
//                        menuItemId +
//                        " in cart of user " +
//                        userId);
//            }
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public void deleteItem(long userId, long menuItemId) throws ServletException {
//        String query = "delete from public.user_cart " +
//                "where user_id = ? and menu_item_id = ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement =
//                     connection.prepareStatement(query)) {
//
//            preparedStatement.setLong(1, userId);
//            preparedStatement.setLong(2, menuItemId);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public void deleteAll(long userId) throws ServletException {
//        String query = "delete from public.user_cart " +
//                "where user_id = ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement =
//                     connection.prepareStatement(query)) {
//
//            preparedStatement.setLong(1, userId);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//
//    private boolean isAlreadyAdded(Connection connection, long userId, long menuItemId) throws ServletException {
//        String query = "select 1 from public.user_cart " +
//                "where user_id = ? and menu_item_id = ?";
//        try (PreparedStatement preparedStatement =
//                     connection.prepareStatement(query)) {
//
//            preparedStatement.setLong(1, userId);
//            preparedStatement.setLong(2, menuItemId);
//            return preparedStatement.executeQuery().next();
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    private List<CartItem> retrieveCartItems(ResultSet resultSet) throws ServletException {
//        try {
//            List<CartItem> cartItems = new ArrayList<>();
//            while (resultSet.next()) {
//                CartItem cartItem = new CartItem(
//                        resultSet.getLong("id"),
//                        resultSet.getString("name"),
//                        resultSet.getDouble("price"),
//                        resultSet.getInt("volume"),
//                        resultSet.getString("image"),
//                        resultSet.getInt("quantity")
//                );
//                cartItems.add(cartItem);
//            }
//
//            resultSet.close();
//            return cartItems;
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//}
