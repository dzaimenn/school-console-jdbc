package dzaimenko.dao.impl;

import dzaimenko.dao.GroupDAO;
import dzaimenko.model.Group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GroupDAOImpl implements GroupDAO {

    private final Connection connection;

    public GroupDAOImpl(Connection connection) {
        this.connection = connection;
    }

    public Map<Group, Integer> findGroupsByMinStudentsCount() {

        Map<Group, Integer> groups = new HashMap<>();

        String sqlFindGroupsByMaxStudentsCount = """
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
        try (PreparedStatement ps = connection.prepareStatement(sqlFindGroupsByMaxStudentsCount)) {

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    Group group = Group.builder()
                            .groupId(rs.getInt("group_id"))
                            .groupName(rs.getString("group_name"))
                            .build();

                    int studentCount = rs.getInt("student_count");

                    groups.put(group, studentCount);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return groups;
    }

}
