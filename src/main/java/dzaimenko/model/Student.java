package dzaimenko.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Student {

    private int studentId;
    private int groupId;
    private String firstName;
    private String lastName;

}