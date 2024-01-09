package dzaimenko;

import dzaimenko.util.DatabaseConnector;

public class DatabaseManager {
    public void shutdown() {
        DatabaseConnector.closeConnection(DatabaseConnector.connect());
        System.out.println("""
                Exiting the program
                """);
        System.exit(0);
    }
}
