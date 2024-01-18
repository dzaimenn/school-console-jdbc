package dzaimenko;

import dzaimenko.model.Group;
import dzaimenko.model.Student;
import dzaimenko.util.SchoolData;

import java.sql.*;
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

        String insertGroupsQuery = "INSERT INTO groups (group_name) VALUES (?)";

        try (PreparedStatement ps = connection.prepareStatement(insertGroupsQuery, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 0; i < 10; i++) {

                String groupName = SchoolData.groupsNames[i];

                Group group = new Group(groupName);

                ps.setString(1, group.getGroupName());
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        group.setGroupId(rs.getInt(1));
                    } else {
                        throw new SQLException("Failed to retrieve generated keys.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void studentsTableFill() {

        String insertStudentsQuery = "INSERT INTO students (group_id, first_name, last_name) VALUES (?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(insertStudentsQuery, Statement.RETURN_GENERATED_KEYS)) {
            for (int i = 1; i <= 200; i++) {

                int groupId = random.nextInt(10) + 1;
                String firstName = SchoolData.firstNamesArray[random.nextInt(20)];
                String lastName = SchoolData.lastNamesArray[random.nextInt(20)];

                Student student = new Student(groupId, firstName, lastName);

                ps.setInt(1, student.getGroupId());
                ps.setString(2, student.getFirstName());
                ps.setString(3, student.getLastName());

                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        student.setStudentId(rs.getInt(1));
                    } else {
                        throw new SQLException("Failed to retrieve generated keys.");
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void coursesTableFill() {

        String insertCoursesQuery = "INSERT INTO courses (course_name, course_description) VALUES (?,?)";

        try (PreparedStatement ps = connection.prepareStatement(insertCoursesQuery)) {
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


        String insertStudentCoursesQuery = "INSERT INTO student_courses (student_id, course_id) VALUES (?,?)";

        try (PreparedStatement ps = connection.prepareStatement(insertStudentCoursesQuery, Statement.RETURN_GENERATED_KEYS)) {

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
