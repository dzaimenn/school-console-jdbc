package dzaimenko.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Course {

    private int courseId;
    private String courseName;
    private String courseDescription;

    public Course(String courseName, String courseDescription) {
        this.courseName = courseName;
        this.courseDescription = courseDescription;
    }


    public Course(int courseId, String courseName) {
        this.courseId = courseId;
        this.courseName = courseName;
    }

}