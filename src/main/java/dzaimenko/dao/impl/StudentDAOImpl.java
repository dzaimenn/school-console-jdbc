package dzaimenko.dao.impl;

import dzaimenko.Main;
import dzaimenko.dao.StudentDAO;
import dzaimenko.model.Course;
import dzaimenko.model.Student;
import dzaimenko.util.SchoolData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class StudentDAOImpl implements StudentDAO {

    private final Connection connection;

    public StudentDAOImpl(Connection connection) {
        this.connection = connection;
    }
    public void findStudentsByCourseName(String course) {

        String sqlFindStudentsByCourse = """
                SELECT students.student_id, students.first_name, students.last_name
                FROM students
                JOIN student_courses ON students.student_id = student_courses.student_id
                JOIN courses ON student_courses.course_id = courses.course_id
                WHERE courses.course_name = ?
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlFindStudentsByCourse)) {
            ps.setString(1, course);

            try (ResultSet rs = ps.executeQuery()) {

                int studentNumber = 0;

                while (rs.next()) {

                    String studentFirstName = rs.getString("first_name");
                    String studentLastName = rs.getString("last_name");

                    Student student = new Student(studentFirstName, studentLastName);

                    studentNumber++;

                    System.out.println(studentNumber + ". " + student.getFirstName() + " " + student.getLastName());
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void addNewStudent(String firstName, String lastName) {

        Student student = new Student(firstName, lastName);

        String sqlAddNewStudent = """
                INSERT INTO students (first_name, last_name)
                VALUES (?,?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlAddNewStudent)) {
            ps.setString(1, student.getFirstName());
            ps.setString(2, student.getLastName());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    public void deleteStudentById(int iD) {

        Student student = new Student(iD);

        String sqlDeleteStudentById = """
                WITH deleted_student_courses AS (
                    DELETE FROM student_courses
                    WHERE student_id = ?
                    RETURNING *
                )
                DELETE FROM students
                WHERE student_id = ?;;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlDeleteStudentById)) {
            ps.setInt(1, student.getStudentId());
            ps.setInt(2, student.getStudentId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addStudentToCourse(int idStudentToAddToCourse, int idCourse) {

        String sqlAddStudentToCourse = """
                INSERT INTO student_courses (student_id, course_id)
                VALUES (?, ?);
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlAddStudentToCourse)) {
            ps.setInt(1, idStudentToAddToCourse);
            ps.setInt(2, idCourse);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void removeStudentFromCourse(int idStudentToRemoveFromCourse, int idCourse) {

        String sqlRemoveStudentFromCourse = """
                DELETE FROM student_courses
                WHERE student_id = ? AND course_id = ?;
                """;

        try (PreparedStatement ps = connection.prepareStatement(sqlRemoveStudentFromCourse)) {
            ps.setInt(1, idStudentToRemoveFromCourse);
            ps.setInt(2, idCourse);

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
