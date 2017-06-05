package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.StudentsNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "STUDENTS")
public class Students implements Serializable {

    @Transient
    private Action action;

    private int idStudent;

    private Users user;

    private Set<StudentsClasses> studentsClasses = new HashSet<>(0);

    private Set<Grades> grades = new HashSet<Grades>(0);

    public Students(StudentsNH studentsNH) {
        this.idStudent = studentsNH.getIdStudent();
        this.user = new Users(studentsNH.getUser());
    }

    @Id
    @SequenceGenerator(name="students_seq", sequenceName = "STUDENTS_SEQ", initialValue = 1100, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "students_seq")
    @Column(name = "ID_STUDENT")
    public int getIdStudent() {
        return idStudent;
    }

    @OneToOne
    @JoinColumn(name = "ID_USER")
    public Users getUser() {
        return user;
    }

    @OneToMany(mappedBy = "student")
    public Set<StudentsClasses> getStudentsClasses() {
        return studentsClasses;
    }

    public void setStudentsClasses(Set<StudentsClasses> studentsClasses) {
        this.studentsClasses = studentsClasses;
    }

    @OneToMany(mappedBy = "student")
    public Set<Grades> getGrades() {
        return grades;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setIdStudent(int idStudent) {
        this.idStudent = idStudent;
    }

    public void setGrades(Set<Grades> grades) {
        this.grades = grades;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public Students(Users user, Set<StudentsClasses> studentsClasses) {
        this.user = user;
        this.studentsClasses = studentsClasses;
    }

    public Students() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Students)) return false;

        Students students = (Students) o;

        if (idStudent != students.idStudent) return false;
        return user != null ? user.equals(students.user) : students.user == null;
    }

    @Override
    public int hashCode() {
        int result = idStudent;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Students{" +
                "idStudent=" + idStudent +
                ", user=" + user +
                '}';
    }

}