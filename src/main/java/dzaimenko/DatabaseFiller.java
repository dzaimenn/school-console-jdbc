package dzaimenko;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DatabaseFiller {

    //    public static String URL = "jdbc:postgresql://localhost:5432/school_db";
    Random random = new Random();
    public static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    public static final String USER = "postgres";
    public static final String PASSWORD = "0017";
    public static final String INIT_SCRIPT_PATH = "path/to/init.sql";

    public void fillDataBase() {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            groupsTableFill(connection);
            studentsTableFill(connection);
            coursesTableFill(connection);
            studentsCoursesTableFill(connection);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void groupsTableFill(Connection connection) {


        String sql = "INSERT INTO groups (group_name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < 10; i++) {
                ps.setString(1, Data.groupsNames[i]);

                ps.execute();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void studentsTableFill(Connection connection) {

        String sql = "INSERT INTO students (group_id, first_name, last_name) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 200; i++) {
                ps.setInt(1, Data.random.nextInt(10) + 1);
                ps.setString(2, Data.firstNamesArray[random.nextInt(20)]);
                ps.setString(3, Data.lastNamesArray[random.nextInt(20)]);

                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void coursesTableFill(Connection connection) {


        String sql = "INSERT INTO courses (course_name, course_description) VALUES (?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < 10; i++) {
                ps.setString(1, Data.coursesNames[i]);
                ps.setString(2, Data.coursesDescriptions[i]);

                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void studentsCoursesTableFill(Connection connection) {


        String sql = "INSERT INTO student_courses (student_id, course_id) VALUES (?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            for (int studentId = 1; studentId <= 200; studentId++) {
                Set<Integer> assignedCourses = new HashSet<>();
                int numberOfCourses = random.nextInt(3) + 1;

                while (assignedCourses.size() < numberOfCourses) {
                    int courseId = random.nextInt(10) + 1;

                    if (assignedCourses.add(courseId)) {
                        ps.setInt(1, studentId);
                        ps.setInt(2, courseId);

                        ps.execute();
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
