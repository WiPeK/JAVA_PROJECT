package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.StudentsClassesNH;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Acer on 24.04.2017.
 */
@Entity
@Table(name = "STUDENTS_CLASSES")
public class StudentsClasses implements Serializable {

    @Transient
    private Action action;

    private int idStudentsClasses;

    private Students student;

    private Classes classes;

    public StudentsClasses(StudentsClassesNH studentsClassesNH) {
        this.idStudentsClasses = studentsClassesNH.getIdStudentsClasses();
        this.student = new Students(studentsClassesNH.getStudent());
        this.classes = new Classes(studentsClassesNH.getClasses());
    }

    @Id
    @SequenceGenerator(name="classes_seq", sequenceName = "CLASSES_SEQ", initialValue = 500, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classes_seq")
    @Column(name = "ID_STUDENTS_CLASSES")
    public int getIdStudentsClasses() {
        return idStudentsClasses;
    }

    public void setIdStudentsClasses(int idStudentsClasses) {
        this.idStudentsClasses = idStudentsClasses;
    }

    @ManyToOne
    @JoinColumn(name = "ID_STUDENT", nullable = false)
    public Students getStudent() {
        return student;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    @ManyToOne
    @JoinColumn(name = "ID_CLASS", nullable = false)
    public Classes getClasses() {
        return classes;
    }

    public void setClasses(Classes classes) {
        this.classes = classes;
    }

    public StudentsClasses(Students student, Classes classes) {
        this.student = student;
        this.classes = classes;
    }

    public StudentsClasses() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StudentsClasses)) return false;

        StudentsClasses that = (StudentsClasses) o;

        if (idStudentsClasses != that.idStudentsClasses) return false;
        if (!student.equals(that.student)) return false;
        return classes.equals(that.classes);
    }

    @Override
    public int hashCode() {
        int result = idStudentsClasses;
        result = 31 * result + (student != null ? student.hashCode() : 0);
        result = 31 * result + (classes != null ? classes.hashCode() : 0);
        return result;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }
}
