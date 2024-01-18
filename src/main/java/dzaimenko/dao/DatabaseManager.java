package dzaimenko.dao;

import dzaimenko.util.DatabaseConnector;
import dzaimenko.util.SchoolData;

import java.sql.*;
import java.util.Scanner;

public class DatabaseManager {

    private final Connection connection;
    private int totalStudents;

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

                while (rs.next()) {

                    int groupId = rs.getInt("group_id");
                    String groupName = rs.getString("group_name");
                    int studentCount = rs.getInt("student_count");

                    System.out.println("Group ID: " + groupId + ", Group Name: " + groupName + ", Student Count: " + studentCount);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void findStudentsByCourseName() {

        showAllCourses();

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

    private int getNumberOfStudents() {
        String sql = "SELECT COUNT(*) AS total_students FROM students";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    totalStudents = rs.getInt("total_students");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return totalStudents;
    }

    public void deleteStudentById() {
        getNumberOfStudents();
        showAllStudents();
        String prompt = "Enter the ID of the student to be removed (from 1 to " + totalStudents + "):";

        System.out.println("The school has " + totalStudents + " students");

        String sql = """
                WITH deleted_student_courses AS (
                    DELETE FROM student_courses
                    WHERE student_id = ?
                    RETURNING *
                )
                DELETE FROM students
                WHERE student_id = ?;;
                """;

        int idStudentToDelete = validateNumericInput(scanner, prompt, 1, totalStudents);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idStudentToDelete);
            ps.setInt(2, idStudentToDelete);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Deleted student");
            } else {
                System.out.println("Failed to delete student");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void showAllStudents() {
        String sql = "SELECT * FROM students";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");

                    System.out.println("ID: " + studentId + ", Student: " + firstName + " " + lastName);
                }

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAllCourses() {
        for (int i = 0; i < 10; i++) {
            System.out.println((i + 1) + ". " + SchoolData.coursesNames[i]);
        }
    }

    public void addStudentToCourse() {
        getNumberOfStudents();
        showAllStudents();

        String promptStudentAdd = "Enter the student ID to add to the course:";
        int idStudentToAddToCourse = validateNumericInput(scanner, promptStudentAdd, 1, totalStudents);

        String promptCourseAdd = "Enter course number:";
        showAllCourses();
        int idCourse = validateNumericInput(scanner, promptCourseAdd, 1, 10);

        String sql = """
                INSERT INTO student_courses (student_id, course_id)
                VALUES (?, ?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idStudentToAddToCourse);
            ps.setInt(2, idCourse);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student successfully added to the course");
            } else {
                System.out.println("Failed to add student to the course");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    private void showCoursesForStudent(int studentId) {
        String sqlSelectCourses = """
                SELECT courses.course_id, courses.course_name
                FROM student_courses
                JOIN courses ON student_courses.course_id = courses.course_id
                WHERE student_courses.student_id = ?;
                """;

        try (PreparedStatement psSelectCourses = connection.prepareStatement(sqlSelectCourses)) {
            psSelectCourses.setInt(1, studentId);

            ResultSet resultSet = psSelectCourses.executeQuery();

            while (resultSet.next()) {
                System.out.println(resultSet.getInt("course_id") + ". "
                                   + resultSet.getString("course_name"));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeStudentFromCourse() {
        getNumberOfStudents();
        showAllStudents();

        String promptStudentRemove = "Enter the student ID to remove from the course:";
        int idStudentToRemoveFromCourse = validateNumericInput(scanner, promptStudentRemove, 1, totalStudents);

        String promptCourseRemove = "Enter course ID:";
        showCoursesForStudent(idStudentToRemoveFromCourse);
        int idCourse = validateNumericInput(scanner, promptCourseRemove, 1, 10);

        String sql = """
                DELETE FROM student_courses
                WHERE student_id = ? AND course_id = ?;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idStudentToRemoveFromCourse);
            ps.setInt(2, idCourse);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Student successfully removed from the course");
            } else {
                System.out.println("Failed to remove student from the course");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        DatabaseConnector.closeConnection();
        System.out.println("""
                Exiting the program
                """);
        System.exit(0);
    }
}
