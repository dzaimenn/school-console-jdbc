package dzaimenko.dao;

public interface StudentDAO {

    void findStudentsByCourseName();
    void addNewStudent();
    void deleteStudentById();
    void addStudentToCourse();
    void removeStudentFromCourse();

}
