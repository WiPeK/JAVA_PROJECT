package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Students;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class StudentsNH implements Serializable {
    private Action action;

    private int idStudent;

    private UsersNH user;

    private Set<StudentsClassesNH> studentsClasses = new HashSet<>(0);

    private Set<GradesNH> grades = new HashSet<>(0);

    public StudentsNH(Students students) {
        this.idStudent = students.getIdStudent();
    }

    public StudentsNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdStudent() {
        return idStudent;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public UsersNH getUser() {
        return user;
    }

    public void setUser(UsersNH user) {
        this.user = user;
    }

    public Set<StudentsClassesNH> getStudentsClasses() {
        return studentsClasses;
    }

    public void setStudentsClasses(Set<StudentsClassesNH> studentsClasses) {
        this.studentsClasses = studentsClasses;
    }

    public Set<GradesNH> getGrades() {
        return grades;
    }

    public void setGrades(Set<GradesNH> grades) {
        this.grades = grades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentsNH)) return false;

        StudentsNH that = (StudentsNH) o;

        return idStudent == that.idStudent;
    }

    @Override
    public int hashCode() {
        return idStudent;
    }

    @Override
    public String toString() {
        return "StudentsNH{" +
                "idStudent=" + idStudent +
                '}';
    }
}
