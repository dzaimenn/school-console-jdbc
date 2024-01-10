package dzaimenko;

import dzaimenko.util.DatabaseConnector;
import dzaimenko.util.SchoolData;

import java.sql.*;
import java.util.Scanner;

public class DatabaseManager {

    private final Connection connection;

    Scanner scanner = new Scanner(System.in);

    public DatabaseManager(Connection connection) {
        this.connection = connection;
    }

    private int validateNumericInput(Scanner scanner, String prompt, int lowerBound, int upperBound) {

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


    public void findGroupsByMaxStudentsCount() {

        String sql = """
                WITH GroupStudentCount AS (
                    SELECT g.group_id, g.group_name, COUNT(s.student_id) as student_count
                    FROM groups g
                             LEFT JOIN students s ON g.group_id = s.group_id
                    GROUP BY g.group_id, g.group_name
                )
                SELECT group_id, group_name, student_count
                FROM GroupStudentCount
                WHERE student_count = (SELECT MIN(student_count) FROM GroupStudentCount);
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            try (ResultSet rs = ps.executeQuery()) {

                int groupId = rs.getInt("group_id");
                String groupName = rs.getString("group_name");
                int studentCount = rs.getInt("student_count");

                while (rs.next()) {
                    System.out.println("Group ID: " + groupId + ", Group Name: " + groupName + ", Student Count: " + studentCount);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void findStudentsByCourseName() {

        for (int i = 0; i < 10; i++) {
            System.out.println((i + 1) + ". " + SchoolData.coursesNames[i]);
        }

        String prompt = "Enter course number:";
        String course = SchoolData.coursesNames[(validateNumericInput(scanner, prompt, 1, 10) - 1)];

        String sql = """
                SELECT students.student_id, students.first_name, students.last_name
                FROM students
                JOIN student_courses ON students.student_id = student_courses.student_id
                JOIN courses ON student_courses.course_id = courses.course_id
                WHERE courses.course_name = ?
                """;

        System.out.println("Students studying " + course);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, course);

            try (ResultSet rs = ps.executeQuery()) {

                int studentNumber = 0;

                while (rs.next()) {
                    String studentFirstName = rs.getString("first_name");
                    String studentLastName = rs.getString("last_name");
                    studentNumber++;
                    System.out.println(studentNumber + ". " + studentFirstName + " " + studentLastName);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void addNewStudent() {

        System.out.println("Enter the first name of the new student:");
        String firstName = scanner.nextLine();

        System.out.println("Enter the last name of the new student:");
        String lastName = scanner.nextLine();

        String sql = """
                INSERT INTO students (first_name, last_name)
                VALUES (?,?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, firstName);
            ps.setString(2, lastName);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Added new student");
            } else {
                System.out.println("Failed to add new student");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStudentById() {

    }

    public void addStudentToCourse() {

    }

    public void removeStudentFromCourse() {

    }

    public void shutdown() {
        DatabaseConnector.closeConnection(DatabaseConnector.connect());
        System.out.println("""
                Exiting the program
                """);
        System.exit(0);
    }
}
