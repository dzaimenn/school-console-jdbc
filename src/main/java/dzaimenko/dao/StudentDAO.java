package dzaimenko.dao;

public interface StudentDAO {

    void findStudentsByCourseName(String course);
    void addNewStudent(String firstname, String lastName);
    void deleteStudentById(int iD);
    void addStudentToCourse(int idStudentToAddToCourse, int idCourse);
    void removeStudentFromCourse(int idStudentToRemoveFromCourse, int idCourse);

}
