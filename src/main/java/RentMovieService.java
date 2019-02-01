import java.math.BigDecimal;
import java.sql.*;
import java.time.Duration;
import java.time.LocalDate;

public class RentMovieService {

    public static void main(String[] args) {
        try (Connection connection = DriverManager.getConnection(args[0], args[1], args[2])) {
            System.out.println("[INFO] Connected to database");

            final int customerId = 1;
            final int copyId = 3;
            rentCopyToCustomer(copyId, customerId, connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private static void rentCopyToCustomer(int copyId, int customerId, Connection connection) throws SQLException {
        int isCopyUpdated = updateMoviesCopies(copyId, 1, getRentedTimes(copyId, connection) + 1, customerId, connection);
        if (isCopyUpdated > 0) {
            int insertSuccess = insertIntoRents(copyId, customerId, RentStatus.IN_RENT,
                    getRentPricePerDayStartValue(customerId, connection), java.sql.Date.valueOf(LocalDate.now()), connection); //getRentPricePerDayStartValue(getReleaseDate(copyId))
            if (insertSuccess > 0)
                System.out.println(String.format("[INFO] The film about id %d was properly loaned out to the user with ID %d", copyId, customerId));
        } else
            updateMoviesCopies(copyId, getIsCopyRentedStatus(copyId, connection), getRentedTimes(copyId, connection) - 1, customerId, connection);
    }


    private static int insertIntoRents(final int copyId, final int customer, final RentStatus status,
                                           final BigDecimal rentPricePerDay, final java.sql.Date rentedDate, final Connection connection) throws SQLException { // <=== NAPISAC
        String parametrizedQuery = "INSERT INTO rents (rentedMovieId, customer, status, rentPricePerDay, rentedDate) VALUES(?,?,?,?,?)";

        PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery);
        preparedStatement.setInt(1, copyId);
        preparedStatement.setInt(2, customer);
        preparedStatement.setString(3, status.getStatus());
        preparedStatement.setDouble(4, Double.valueOf(rentPricePerDay.toPlainString()));
        preparedStatement.setDate(5, rentedDate);


        return preparedStatement.executeUpdate();
    }

    //java.sql.Date -> LocalDate
    private static LocalDate getReleaseDate(final int copyId) { // <=== NAPISAC
        String parametrizedQuery = "";


        return null;
    }

    private static BigDecimal getRentPricePerDayStartValue(final int rentedMovieId, Connection connection) throws SQLException {
        String parametrizedQuery = "SELECT rentPricePerDay from rents where rentedMovieId = ?";

        BigDecimal rentPricePerDay = new BigDecimal("0.00");
        try (PreparedStatement preparedStatement = connection.prepareStatement(parametrizedQuery)) {
            preparedStatement.setInt(1, rentedMovieId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    rentPricePerDay = resultSet.getBigDecimal("rentPricePerDay");
                }
            }
        }
        return rentPricePerDay;
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

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next())
                    result = resultSet.getInt("isRented");
            }
        }
        return result;
    }
}