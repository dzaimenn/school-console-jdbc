package dzaimenko.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Group {

    private int groupId;
    private String groupName;

}