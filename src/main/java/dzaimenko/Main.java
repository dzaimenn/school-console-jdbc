package dzaimenko;

import dzaimenko.dao.GroupDAO;
import dzaimenko.dao.impl.GroupDAOImpl;
import dzaimenko.util.DatabaseConnector;
import dzaimenko.util.MenuManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        String sqlScriptPath = "/init.sql";

        try (Connection connection = DatabaseConnector.getInstance().getConnection();
             Statement statement = connection.createStatement();
             Scanner scanner = new Scanner(System.in)) {

            Map<Integer, Runnable> options = new HashMap<>();
            MenuManager menuManager = new MenuManager(options, connection, scanner);
            GroupDAO courseDAO = new GroupDAOImpl(connection);

            options.put(1, menuManager::menuFindGroupsByMinStudentsCount);
            options.put(2, menuManager::menuFindStudentsByCourseName);
            options.put(3, menuManager::menuAddNewStudent);
            options.put(4, menuManager::menuDeleteStudentById);
            options.put(5, menuManager::menuAddStudentToCourse);
            options.put(6, menuManager::menuRemoveStudentFromCourse);
            options.put(0, Main::shutdown);

            executeSqlScript(statement, sqlScriptPath);
            fillDatabase(connection);
            menuManager.requestManagement();


        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeSqlScript(Statement statement, String sqlScriptPath) throws IOException, SQLException {
        InputStream inputStream = Main.class.getResourceAsStream(sqlScriptPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sqlScript = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            sqlScript.append(line).append("\n");
        }

        statement.execute(sqlScript.toString());
        System.out.println("SQL script executed successfully");
    }

    private static void fillDatabase(Connection connection) {
        DatabaseFiller databaseFiller = new DatabaseFiller(connection);
        databaseFiller.fillDataBase();
        System.out.println("Database filled successfully");
    }

    public static int validateNumericInput(Scanner scanner, String prompt, int lowerBound, int upperBound) {

        int maxRequests = 0;
        final int maxAttempts = 3;
        int option = -1;

        while (maxRequests < maxAttempts) {
            System.out.println(prompt);

            try {
                option = Integer.parseInt(scanner.nextLine());

                if (option >= lowerBound && option <= upperBound) {
                    return option;
                } else {
                    System.out.println("Invalid input. Please enter a number within the specified range.");
                    maxRequests++;
                }
            } catch (NumberFormatException e) {
                if (maxRequests < maxAttempts - 1) {
                    System.out.println("Invalid input. Please enter a valid numeric request. Try again.");
                }
                maxRequests++;
            }

            if (maxRequests == maxAttempts) {
                System.out.println("You have entered incorrect instructions multiple times. Exiting the program.");
                System.exit(0);
            }
        }

        return option;
    }

    public static void shutdown() {
        DatabaseConnector.closeConnection();
        System.out.println("""
                Exiting the program
                """);
        System.exit(0);
    }

}

