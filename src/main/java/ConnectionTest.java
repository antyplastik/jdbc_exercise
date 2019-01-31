import java.sql.*;

public class ConnectionTest {

    public static void main(String[] args) {
        establishConnectionTest(args);

        try {
            Connection connection = DriverManager.getConnection(args[0], args[1], args[2]);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println(queryResoult(args, "SELECT title, releaseDate, description FROM moviesinfo "));
//        addDescToMovieInfo(1, "this film is about nothing", connection);


    }

    public static void establishConnectionTest(String[] args) {
        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2])) {
            System.out.println("[INFO] Established connection with database");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void addDescToMovieInfo(final int movieInfoId, final String desc, final Connection connection) {
        String parametrizedQuery = "UPDATE moviesInfo SET description = ? WHERE movieInfoId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery)) {
            preparedStatement.setString(1, desc);
            preparedStatement.setInt(2, movieInfoId);

            int rowsUpdated = preparedStatement.executeUpdate();
            System.out.printf("Zaktualizowano %d rekordow w tabeli moviesinfo", rowsUpdated);

        } catch (SQLException ex) {
            System.out.println(ex);
        }
    }

    public static String queryResoult(String[] args, String query) {
        String result = "";
        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2])) {
            Statement statement = connection.createStatement();
            try (ResultSet resultSet = statement.executeQuery(query)) {
                {
                    while (resultSet.next())
                        result = result + "\n" + resultSet.getString("title") + " | "
                                + resultSet.getString("releaseDate") + " | "
                                + resultSet.getString("description");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
