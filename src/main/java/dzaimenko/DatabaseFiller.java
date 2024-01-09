package dzaimenko;

import dzaimenko.util.SchoolData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class DatabaseFiller {
    Random random = new Random();
    private final Connection connection;

    public DatabaseFiller(Connection connection) {
        this.connection = connection;
    }

    public void fillDataBase() {

            groupsTableFill();
            studentsTableFill();
            coursesTableFill();
            studentsCoursesTableFill();
    }

    private void groupsTableFill() {


        String sql = "INSERT INTO groups (group_name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < 10; i++) {
                ps.setString(1, SchoolData.groupsNames[i]);

                ps.execute();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void studentsTableFill() {

        String sql = "INSERT INTO students (group_id, first_name, last_name) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 1; i <= 200; i++) {
                ps.setInt(1, SchoolData.random.nextInt(10) + 1);
                ps.setString(2, SchoolData.firstNamesArray[random.nextInt(20)]);
                ps.setString(3, SchoolData.lastNamesArray[random.nextInt(20)]);

                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void coursesTableFill() {


        String sql = "INSERT INTO courses (course_name, course_description) VALUES (?,?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            for (int i = 0; i < 10; i++) {
                ps.setString(1, SchoolData.coursesNames[i]);
                ps.setString(2, SchoolData.coursesDescriptions[i]);

                ps.execute();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void studentsCoursesTableFill() {


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
