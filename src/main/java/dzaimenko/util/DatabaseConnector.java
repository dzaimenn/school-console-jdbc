package dzaimenko.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnector {

    private DatabaseConnector() {

    }

    private static final String PROPERTY_FILE = "db.properties";
    private static final String URL = "db.url";
    private static final String USER = "db.user";
    private static final String PASSWORD = "db.password";
    private static final String SCHEMA = "db.schema";
    private static final Properties properties = new Properties();
    private static Connection connection;

    static {
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream(PROPERTY_FILE)) {
            properties.load(input);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load properties file", e);
        }
    }

    public static Connection connect() {

        try {
            if (connection == null || connection.isClosed()) {
                try {
                    connection = DriverManager.getConnection(
                            properties.getProperty(URL),
                            properties.getProperty(USER),
                            properties.getProperty(PASSWORD)
                    );
                    connection.setSchema(properties.getProperty(SCHEMA));

                    return connection;

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

            } else return connection;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
