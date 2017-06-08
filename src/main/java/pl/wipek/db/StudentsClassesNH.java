package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.StudentsClasses;

import java.io.Serializable;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class StudentsClassesNH implements Serializable{

    private Action action;

    private int idStudentsClasses;

    private StudentsNH student;

    private ClassesNH classes;

    public StudentsClassesNH(StudentsClasses studentsClasses) {
        this.idStudentsClasses = studentsClasses.getIdStudentsClasses();
        this.student = new StudentsNH(studentsClasses.getStudent());
        this.classes = new ClassesNH(studentsClasses.getClasses());
    }

    public StudentsClassesNH(StudentsNH student, ClassesNH classes) {
        this.student = student;
        this.classes = classes;
    }

    public StudentsClassesNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdStudentsClasses() {
        return idStudentsClasses;
    }

    public void setIdStudentsClasses(int idStudentsClasses) {
        this.idStudentsClasses = idStudentsClasses;
    }

    public StudentsNH getStudent() {
        return student;
    }

    public void setStudent(StudentsNH student) {
        this.student = student;
    }

    public ClassesNH getClasses() {
        return classes;
    }

    public void setClasses(ClassesNH classes) {
        this.classes = classes;
    }

    @Override
    public String toString() {
        return "StudentsClassesNH{" +
                "idStudentsClasses=" + idStudentsClasses +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentsClassesNH)) return false;

        StudentsClassesNH that = (StudentsClassesNH) o;

        if (idStudentsClasses != that.idStudentsClasses) return false;
        if (student != null ? !student.equals(that.student) : that.student != null) return false;
        return classes != null ? classes.equals(that.classes) : that.classes == null;
    }

    @Override
    public int hashCode() {
        return idStudentsClasses;
    }
}
