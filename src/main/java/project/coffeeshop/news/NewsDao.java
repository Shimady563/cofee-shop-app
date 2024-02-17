package project.coffeeshop.news;

import jakarta.servlet.ServletException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NewsDao {
    private final DataSource dataSource;

    public NewsDao() throws ServletException {
        try {
            Context context = new InitialContext();
            Context envContext = (Context) context.lookup("java:comp/env");
            this.dataSource = (DataSource) envContext.lookup("jdbc/db");
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }

    public List<PieceOfNews> findAll() throws ServletException {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            return getNews(statement.executeQuery("select * from public.news"));
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    public List<PieceOfNews> searchByTitle(String key) throws ServletException {
        String query = "select * from public.news " +
                "where title ilike ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection
                     .prepareStatement(query)) {

            preparedStatement.setString(1, "%" + key + "%");
            return getNews(preparedStatement.executeQuery());
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    private List<PieceOfNews> getNews(ResultSet resultSet) throws SQLException {
        List<PieceOfNews> news = new ArrayList<>();
        while (resultSet.next()) {
            PieceOfNews pieceOfNews = new PieceOfNews(
                    resultSet.getLong(1),
                    resultSet.getString(2),
                    resultSet.getTimestamp(3).toLocalDateTime(),
                    resultSet.getString(4),
                    resultSet.getString(5)
            );
            news.add(pieceOfNews);
        }

        resultSet.close();
        return news;
    }
}
