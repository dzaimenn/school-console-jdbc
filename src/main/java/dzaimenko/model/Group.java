package dzaimenko.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Group {

    private int groupId;
    private String groupName;

    public Group(String groupName) {
        this.groupName = groupName;
    }

    public Group(int groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

}