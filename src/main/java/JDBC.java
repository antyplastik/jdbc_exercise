import java.sql.*;

public class JDBC {

    private Connection connection;

    public JDBC(Connection connection) {
        this.connection = connection;
    }

    public static Connection connectToDB(){

        return null;
    }

    public String updateQuery(String[] args, String query) {
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
