package dzaimenko.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

    private int studentId;
    private int groupId;
    private String firstName;
    private String lastName;

    public Student(int groupId, String firstName, String lastName) {
        this.groupId = groupId;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Student(int studentId) {
        this.studentId = studentId;
    }

}