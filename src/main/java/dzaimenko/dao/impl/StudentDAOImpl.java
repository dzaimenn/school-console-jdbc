package dzaimenko.dao.impl;

import dzaimenko.Main;
import dzaimenko.dao.StudentDAO;
import dzaimenko.util.SchoolData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StudentDAOImpl implements StudentDAO {

    private final Connection connection;
    private final Scanner scanner;
    private int totalStudents;
    public StudentDAOImpl(Connection connection, Scanner scanner) {
        this.connection = connection;
        this.scanner = scanner;
    }

    public void findStudentsByCourseName() {

        showAllCourses();

        String prompt = "Enter course number:";
        String course = SchoolData.coursesNames[(Main.validateNumericInput(scanner, prompt, 1, 10) - 1)];

        String sqlFindStudentsByCourse = """
                SELECT students.student_id, students.first_name, students.last_name
                FROM students
                JOIN student_courses ON students.student_id = student_courses.student_id
                JOIN courses ON student_courses.course_id = courses.course_id
                WHERE courses.course_name = ?
                """;

        System.out.println("Students studying " + course);

        try (PreparedStatement ps = connection.prepareStatement(sqlFindStudentsByCourse)) {
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

        String sqlAddNewStudent = """
                INSERT INTO students (first_name, last_name)
                VALUES (?,?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlAddNewStudent)) {
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

        String sqlGetNumberOfStudents = "SELECT COUNT(*) AS total_students FROM students";
        try (PreparedStatement ps = connection.prepareStatement(sqlGetNumberOfStudents)) {
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

        String sqlDeleteStudentById = """
                WITH deleted_student_courses AS (
                    DELETE FROM student_courses
                    WHERE student_id = ?
                    RETURNING *
                )
                DELETE FROM students
                WHERE student_id = ?;;
                """;

        int idStudentToDelete = Main.validateNumericInput(scanner, prompt, 1, totalStudents);

        try (PreparedStatement ps = connection.prepareStatement(sqlDeleteStudentById)) {
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
        String sqlShowAllStudents = "SELECT * FROM students";

        try (PreparedStatement ps = connection.prepareStatement(sqlShowAllStudents)) {
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
        int idStudentToAddToCourse = Main.validateNumericInput(scanner, promptStudentAdd, 1, totalStudents);

        String promptCourseAdd = "Enter course number:";
        showAllCourses();
        int idCourse = Main.validateNumericInput(scanner, promptCourseAdd, 1, 10);

        String sqlAddStudentToCourse = """
                INSERT INTO student_courses (student_id, course_id)
                VALUES (?, ?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlAddStudentToCourse)) {
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
        int idStudentToRemoveFromCourse = Main.validateNumericInput(scanner, promptStudentRemove, 1, totalStudents);

        String promptCourseRemove = "Enter course ID:";
        showCoursesForStudent(idStudentToRemoveFromCourse);
        int idCourse = Main.validateNumericInput(scanner, promptCourseRemove, 1, 10);

        String sqlRemoveStudentFromCourse = """
                DELETE FROM student_courses
                WHERE student_id = ? AND course_id = ?;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlRemoveStudentFromCourse)) {
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

}
