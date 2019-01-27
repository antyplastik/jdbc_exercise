import java.sql.*;

public class ConnectionTest {

    public static void main(String[] args) {
        establishConnectionTest(args);

        System.out.println(queryResoult(args, "SELECT title, releaseDate, description FROM moviesInfo "));



    }

    public static void establishConnectionTest(String[] args){
        try(Connection connection = DriverManager.getConnection(args[0],args[1],args[2])){
            System.out.println("[INFO] Established connection with database");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String queryResoult(String[] args, String query){
        String result = "";
        try(Connection connection = DriverManager.getConnection(args[0],args[1],args[2])){
            Statement statement = connection.createStatement();
            try (ResultSet resultSet = statement.executeQuery(query)) {
                {
                    while (resultSet.next())
                        result = result + "\n" + resultSet.getString("title") + " | "
                                + resultSet.getString("releaseDate") + " | "
                                + resultSet.getString("description") ;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

}
