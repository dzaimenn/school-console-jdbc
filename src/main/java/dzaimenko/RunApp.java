package dzaimenko;

import dzaimenko.util.DatabaseConnector;

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

public class RunApp {

    private final static String MENU_REQUEST = """
                ______________________________________________________________
                Enter a request from 1 to 6 or 0 to complete the job:
                            
                1. Find all groups with less or equal students’ number
                2. Find all students related to the course with the given name
                3. Add a new student
                4. Delete a student by the STUDENT_ID
                5. Add a student to the course (from a list)
                6. Remove the student from one of their courses
                                    
                0. Exit
                                
                Enter the number of the selected request:""";

    public static void main(String[] args) {

        String sqlScriptPath = "/init.sql";

        try (Connection connection = DatabaseConnector.connect();
             Statement statement = connection.createStatement();
             Scanner scanner = new Scanner(System.in)) {

            DatabaseManager databaseManager = new DatabaseManager();

            Map<Integer, Runnable> options = new HashMap<>();
            options.put(1, databaseManager::shutdown);
            options.put(2, databaseManager::shutdown);
            options.put(3, databaseManager::shutdown);
            options.put(4, databaseManager::shutdown);
            options.put(5, databaseManager::shutdown);
            options.put(6, databaseManager::shutdown);
            options.put(0, databaseManager::shutdown);

            executeSqlScript(statement, sqlScriptPath);
            fillDatabase(connection);
            requestManagement(scanner, options);


        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeSqlScript(Statement statement, String sqlScriptPath) throws IOException, SQLException {
        InputStream inputStream = RunApp.class.getResourceAsStream(sqlScriptPath);
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

    public static void displayMenu() {
        System.out.println(MENU_REQUEST);
    }

    private static void requestManagement(Scanner scanner, Map<Integer, Runnable> options) {
        int maxRequests = 0;

        while (maxRequests < 4) {

            displayMenu();

            try {
                int option = Integer.parseInt(scanner.nextLine());

                if (options.containsKey(option)) {
                    options.get(option).run();
                    maxRequests = 0;
                } else {
                    System.out.println("Invalid input. Please enter a number between 1 and 6 or 0 to exit.");
                    maxRequests++;

                }
            } catch (NumberFormatException e) {
                if (maxRequests < 2) {
                    System.out.println("Invalid input. Please enter a valid numeric request. Try again.");

                }
                maxRequests++;
            }

            if (maxRequests == 3) {
                System.out.println("You have entered incorrect instructions multiple times. Exiting the program.");
                break;
            }
        }
    }

}

