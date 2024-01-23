package dzaimenko.util;

import dzaimenko.Main;
import dzaimenko.dao.GroupDAO;
import dzaimenko.dao.StudentDAO;
import dzaimenko.dao.impl.GroupDAOImpl;
import dzaimenko.dao.impl.StudentDAOImpl;
import dzaimenko.model.Course;
import dzaimenko.model.Student;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuManager {
    private final String MAIN_MENU_REQUEST = """
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

    private final Map<Integer, Runnable> options;
    private final Connection connection;
    private final Scanner scanner;
    private int totalStudents;


    public MenuManager(Map<Integer, Runnable> options, Connection connection, Scanner scanner) {
        this.options = options;
        this.connection = connection;
        this.scanner = scanner;
    }

    public void displayMainMenu() {
        System.out.println(MAIN_MENU_REQUEST);
    }

    public void requestManagement() {
        int maxRequests = 0;

        while (maxRequests < 4) {

            displayMainMenu();

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

    public void menuFindGroupsByMinStudentsCount() {

        System.out.println("Groups with less or equal students");

        GroupDAO groupDAO = new GroupDAOImpl(connection);
        groupDAO.findGroupsByMinStudentsCount();

    }

    public void menuFindStudentsByCourseName() {

        showAllCourses();

        String courseNumberPrompt = "Enter course number:";
        String course = SchoolData.coursesNames[(Main.validateNumericInput(scanner, courseNumberPrompt, 1, 10) - 1)];

        StudentDAO studentDAO = new StudentDAOImpl(connection);
        List<Student> students = studentDAO.findStudentsByCourseName(course);

        if (students.isEmpty()) {
            System.out.println("No students found for the given course.");
            return;
        }

        System.out.println("Students studying " + course+ ":");
        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            System.out.println((i + 1) + ". " + student.getFirstName() + " " + student.getLastName());
        }
    }

    private void showAllCourses() {
        for (int i = 0; i < 10; i++) {
            System.out.println((i + 1) + ". " + SchoolData.coursesNames[i]);
        }
    }

    public void menuAddNewStudent() {

        System.out.println("Enter the first name of the new student:");
        String firstName = scanner.nextLine();

        System.out.println("Enter the last name of the new student:");
        String lastName = scanner.nextLine();

        StudentDAO student = new StudentDAOImpl(connection);
        student.addNewStudent(firstName, lastName);
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

    public void menuDeleteStudentById() {
        getNumberOfStudents();
        showAllStudents();
        String prompt = "Enter the ID of the student to be removed (from 1 to " + totalStudents + "):";

        System.out.println("The school has " + totalStudents + " students");
        int idStudentToDelete = Main.validateNumericInput(scanner, prompt, 1, totalStudents);

        StudentDAO studentDAO = new StudentDAOImpl(connection);
        studentDAO.deleteStudentById(idStudentToDelete);

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

    public void menuAddStudentToCourse() {
        getNumberOfStudents();
        showAllStudents();

        String promptStudentAdd = "Enter the student ID to add to the course:";
        int idStudentToAddToCourse = Main.validateNumericInput(scanner, promptStudentAdd, 1, totalStudents);

        String promptCourseAdd = "Enter course number:";
        showAllCourses();
        int idCourse = Main.validateNumericInput(scanner, promptCourseAdd, 1, 10);

        StudentDAO studentDAO = new StudentDAOImpl(connection);
        studentDAO.addStudentToCourse(idStudentToAddToCourse, idCourse);
    }

    private void showCoursesForStudent(int studentId) {

        Student student = new Student(studentId);

        String sqlSelectCourses = """
                SELECT courses.course_id, courses.course_name
                FROM student_courses
                JOIN courses ON student_courses.course_id = courses.course_id
                WHERE student_courses.student_id = ?;
                """;

        try (PreparedStatement psSelectCourses = connection.prepareStatement(sqlSelectCourses)) {
            psSelectCourses.setInt(1, student.getStudentId());

            ResultSet resultSet = psSelectCourses.executeQuery();

            while (resultSet.next()) {
                Course course = new Course(resultSet.getInt("course_id"), resultSet.getString("course_name"));

                System.out.println(course.getCourseId() + ". " + course.getCourseName());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void menuRemoveStudentFromCourse() {
        getNumberOfStudents();
        showAllStudents();

        String promptStudentRemove = "Enter the student ID to remove from the course:";
        int idStudentToRemoveFromCourse = Main.validateNumericInput(scanner, promptStudentRemove, 1, totalStudents);

        String promptCourseRemove = "Enter course ID:";
        showCoursesForStudent(idStudentToRemoveFromCourse);
        int idCourse = Main.validateNumericInput(scanner, promptCourseRemove, 1, 10);

        StudentDAO studentDAO = new StudentDAOImpl(connection);
        studentDAO.removeStudentFromCourse(idStudentToRemoveFromCourse, idCourse);
    }

}
