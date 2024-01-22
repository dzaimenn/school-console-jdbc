package dzaimenko.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private static final DatabaseConnector INSTANCE = new DatabaseConnector();
    private static Connection connection;

    private DatabaseConnector() {
        try (FileInputStream fis = new FileInputStream("src/main/resources/db.properties")) {
            Properties properties = new Properties();
            properties.load(fis);

            connection = DriverManager.getConnection(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.user"),
                    properties.getProperty("db.password")
            );
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseConnector getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to close the database connection", e);
        }
    }

}