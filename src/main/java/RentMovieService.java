import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;

public class RentMovieService {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2])) {
            System.out.println("[INFO] Connected to database");

            final int customerId = 1;
            final int copyId = 2;
            rentCopyToCustomer(copyId, customerId, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void rentCopyToCustomer(int copyId, int customerId, Connection connection) throws SQLException {
        int isCopyUpdated = updateMoviesCopies(copyId, 1, getRentedTimes(copyId, connection) + 1, customerId, connection);
        if (isCopyUpdated == 1) {
            insertIntoRents(copyId, customerId, RentStatus.IN_RENT,
                    getRentPricePerDay(getReleaseDate(copyId)), java.sql.Date.valueOf(LocalDate.now()), connection);
        } else
            updateMoviesCopies(copyId, getIsCopyRentedStatus(copyId, connection), getRentedTimes(copyId, connection) - 1, customerId, connection);
    }


    private static int insertIntoRents(final int copyId, final int customer, final RentStatus status,
                                       final double rentPricePerDay, final java.sql.Date rentedDate, final Connection connection) throws SQLException { // <=== NAPISAC
        String parametrizedQuery = "INSERT INTO rents (rentedMovieId = ?, isRented = ?, rentPricePerDay = ?, rentedDate = ? WHERE customerId = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery);
        preparedStatement.setInt(1, copyId);
        preparedStatement.setString(2, status.getStatus());
        preparedStatement.setDate(3, rentedDate);
        preparedStatement.setInt(4, customer);

        return preparedStatement.executeUpdate();
    }

    //java.sql.Date -> LocalDate
    private static LocalDate getReleaseDate(final int copyId) { // <=== NAPISAC
        String parametrizedQuery = "";


        return null;
    }

    private static double getRentPricePerDay(final int rentedMovieId){

        return 0;
    }

    private static double getRentPricePerDay(final LocalDate releaseDate) {
        int releasedDaysAgo = (int) Duration.between(LocalDate.now(), releaseDate).toDays();
        if (releasedDaysAgo < 14) {
            return 10.0;
        } else if (releasedDaysAgo >= 14 && releasedDaysAgo < 180) {
            return 5.0;
        } else {
            return 2.5;
        }
    }

    private static int updateMoviesCopies(final int copyId, final int isRented, final int rentedTimes, final int rentedTo, final Connection connection) throws SQLException {
        String parametrizedQuery = "UPDATE moviescopies SET isRented = ?, rentedTimes = ?, rentedTo = ? WHERE copyId = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery);
        preparedStatement.setInt(1, isRented);
        preparedStatement.setInt(2, rentedTimes);
        preparedStatement.setInt(3, rentedTo);
        preparedStatement.setInt(4, copyId);

        return preparedStatement.executeUpdate();
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

    private static int getIsCopyRentedStatus(final int copyId, Connection connection) throws SQLException {
        String parametrizedQuery = "SELECT isRented from moviescopies where copyId = ?";
        int result = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery)) {
            preparedStatement.setInt(1, copyId);

            try(ResultSet resultSet = preparedStatement.executeQuery()){
                while (resultSet.next())
                    result = resultSet.getInt("isRented");
            }
        }
        return result;
    }
}