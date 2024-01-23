package dzaimenko.dao;

import dzaimenko.model.Group;

import java.util.Map;

public interface GroupDAO {

    Map<Group, Integer> findGroupsByMinStudentsCount();

}
