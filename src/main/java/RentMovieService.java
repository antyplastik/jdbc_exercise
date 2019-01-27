import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;

public class RentMovieService {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(args[1], args[2], args[3])) {
            System.out.println("[INFO] Connected to database");

            final int customerId = 1;
            final int copyId = 20;
            rentCopyToCustomer(copyId, customerId, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void rentCopyToCustomer(int copyId, int customerId, Connection connection) throws SQLException {
        boolean isCopyUpdated = updateMoviesCopies(copyId, true, getRentedTimes(copyId, connection) + 1, customerId, connection);
        if (isCopyUpdated) {
            insertIntoRents(copyId, customerId, RentStatus.IN_RENT,
                    getRentPricePerDay(getReleaseDate(copyId)), java.sql.Date.valueOf(LocalDate.now()), connection);
        }
    }


    private static boolean insertIntoRents(final int copyId, final int customer, final RentStatus status,
                                           final double rentPricePerDay, final java.sql.Date rentedDate, final Connection connection) throws SQLException { // <=== NAPISAC
        String parametrizedQuery = "INSERT INTO rents (rentedMovieId = ?, isRented = ?, rentPricePerDay = ?, rentDate = ? WHERE customerId = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery);
        preparedStatement.setInt(1, copyId);
        preparedStatement.setString(2, status.getStatus());
        preparedStatement.setDate(3, rentedDate);
        preparedStatement.setInt(4, customer);

        return preparedStatement.execute();
    }
    //java.sql.Date -> LocalDate
    private static LocalDate getReleaseDate(final int copyId) { // <=== NAPISAC
        String parametrizedQuery = "";



        return null;
    }

    private static double getRentPricePerDay(final LocalDate releaseDate) {
        int releasedDaysAgo = (int) Duration.between(LocalDate.now(), releaseDate).toDays();
        if(releasedDaysAgo < 14) {
            return 10.0;
        } else if(releasedDaysAgo >= 14 && releasedDaysAgo < 180) {
            return 5.0;
        } else {
            return 2.5;
        }
    }

    private static boolean updateMoviesCopies(final int copyId, final boolean isRented, final int rentedTimes, final int rentedTo, final Connection connection) throws SQLException {
        String parametrizedQuery = "UPDATE moviescopies SET isRented = ?, rentedTimes = ?, rentedTo = ? WHERE copyId = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery);
        preparedStatement.setBoolean(1, isRented);
        preparedStatement.setInt(2, rentedTimes);
        preparedStatement.setInt(3, rentedTo);
        preparedStatement.setInt(4, copyId);

        return preparedStatement.execute();
    }

    private static int getRentedTimes(final int copyId, final Connection connection) throws SQLException {
        String parametrizedQuery = "SELECT rentedTimes FROM moviescopies WHERE copyId = ?";
        int rentedTimes = -1;
        try (PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery)) {
            preparedStatement.setInt(1, copyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rentedTimes = resultSet.getInt("rentedTimes");
                }
            }
        }
        return rentedTimes;
    }
}