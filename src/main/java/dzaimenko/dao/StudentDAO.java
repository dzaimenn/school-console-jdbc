package dzaimenko.dao;

import dzaimenko.model.Student;

import java.util.List;

public interface StudentDAO {

    List<Student> findStudentsByCourseName(String course);
    void addNewStudent(String firstname, String lastName);
    void deleteStudentById(int iD);
    void addStudentToCourse(int idStudentToAddToCourse, int idCourse);
    void removeStudentFromCourse(int idStudentToRemoveFromCourse, int idCourse);

}
