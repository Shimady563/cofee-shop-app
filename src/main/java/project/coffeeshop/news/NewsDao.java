package project.coffeeshop.news;

import jakarta.persistence.EntityTransaction;
import project.coffeeshop.commons.AbstractDao;

import java.util.List;
import java.util.Optional;

public class NewsDao extends AbstractDao<PieceOfNews, Long> {

    @Override
    public Optional<PieceOfNews> findById(Long id) {
        return Optional.ofNullable(entityManager.find(PieceOfNews.class, id));
    }

    public List<PieceOfNews> findAll() {
        return entityManager
                .createQuery("select p from PieceOfNews p", PieceOfNews.class)
                .getResultList();
    }

    public List<PieceOfNews> findByTitle(String title) {
        return entityManager
                .createQuery("select p from PieceOfNews p " +
                        "where p.title ilike :title", PieceOfNews.class)
                .setParameter("title", "%" + title + "%")
                .getResultList();
    }
}

//public class NewsDao {
//    private final DataSource dataSource;
//
//    public NewsDao() throws ServletException {
//        try {
//            Context context = new InitialContext();
//            Context envContext = (Context) context.lookup("java:comp/env");
//            this.dataSource = (DataSource) envContext.lookup("jdbc/db");
//        } catch (NamingException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public List<PieceOfNews> findAll() throws ServletException {
//        try (Connection connection = dataSource.getConnection();
//             Statement statement = connection.createStatement()) {
//
//            return getNews(statement.executeQuery("select * from public.news"));
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public List<PieceOfNews> searchByTitle(String key) throws ServletException {
//        String query = "select * from public.news " +
//                "where title ilike ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection
//                     .prepareStatement(query)) {
//
//            preparedStatement.setString(1, "%" + key + "%");
//            return getNews(preparedStatement.executeQuery());
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    public Optional<PieceOfNews> getById(long id) throws ServletException {
//        String query = "select * from public.news " +
//                "where id = ?";
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection
//                     .prepareStatement(query)) {
//
//            preparedStatement.setLong(1, id);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                return Optional.of(new PieceOfNews(
//                        resultSet.getLong(1),
//                        resultSet.getString(2),
//                        resultSet.getTimestamp(3).toLocalDateTime(),
//                        resultSet.getString(4),
//                        resultSet.getString(5)
//                ));
//            }
//
//            return Optional.empty();
//        } catch (SQLException e) {
//            throw new ServletException(e);
//        }
//    }
//
//    private List<PieceOfNews> getNews(ResultSet resultSet) throws SQLException {
//        List<PieceOfNews> news = new ArrayList<>();
//        while (resultSet.next()) {
//            PieceOfNews pieceOfNews = new PieceOfNews(
//                    resultSet.getLong(1),
//                    resultSet.getString(2),
//                    resultSet.getTimestamp(3).toLocalDateTime(),
//                    resultSet.getString(4),
//                    resultSet.getString(5)
//            );
//            news.add(pieceOfNews);
//        }
//
//        resultSet.close();
//        return news;
//    }
//}
