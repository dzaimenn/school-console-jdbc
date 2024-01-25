package dzaimenko.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Course {

    private int courseId;
    private String courseName;
    private String courseDescription;

}