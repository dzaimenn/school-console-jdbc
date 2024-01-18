package dzaimenko.dao.impl;

import dzaimenko.dao.CourseDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CourseDAOImpl implements CourseDAO {

    private Connection connection;

    public CourseDAOImpl(Connection connection) {
        this.connection = connection;
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

}
