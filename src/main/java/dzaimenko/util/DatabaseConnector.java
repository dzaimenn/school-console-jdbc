package dzaimenko.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

    private DatabaseConnector() {

    }
    public static final String URL = "jdbc:postgresql://localhost:5432/school_db?currentSchema=school_management";
    public static final String USER = "school_manager";
    public static final String PASSWORD = "1234";

    public static Connection connect() {

        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            try (Statement statement = connection.createStatement()) {
                statement.execute("SET search_path TO school_management, public");
            }
            return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
