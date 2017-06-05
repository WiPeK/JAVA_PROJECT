package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.ClassesNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "CLASSES", uniqueConstraints = {
        @UniqueConstraint(columnNames = "NAME")
})
public class Classes implements Serializable {

    @Transient
    private Action action;

    private int idClass;

    private String name;

    private Semesters semester;

    private Set<CarriedSubjects> carriedSubjects = new HashSet<>(0);

    private Set<StudentsClasses> studentsClasses = new HashSet<>(0);

    public Classes(ClassesNH classesNH) {
        this.idClass = classesNH.getIdClass();
        this.name = classesNH.getName();
        this.semester = new Semesters(classesNH.getSemester());
    }

    @Id
    @SequenceGenerator(name="classes_seq", sequenceName = "CLASSES_SEQ", initialValue = 500, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classes_seq")
    @Column(name = "ID_CLASS")
    public int getIdClass() {
        return idClass;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "classes", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<CarriedSubjects> getCarriedSubjects() {
        return carriedSubjects;
    }

    @OneToMany(mappedBy = "classes", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<StudentsClasses> getStudentsClasses() {
        return studentsClasses;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCarriedSubjects(Set<CarriedSubjects> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    public void setIdClass(int idClass) {
        this.idClass = idClass;
    }

    public void setStudents(Set<StudentsClasses> studentsClasses) {
        this.studentsClasses = studentsClasses;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_SEMESTER", nullable = false)
    public Semesters getSemester() {
        return semester;
    }

    public void setSemester(Semesters semester) {
        this.semester = semester;
    }

    public void setStudentsClasses(Set<StudentsClasses> studentsClasses) {
        this.studentsClasses = studentsClasses;
    }

    public Classes(String name) {
        this.name = name;
    }

    public Classes() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classes)) return false;

        Classes classes = (Classes) o;

        if (idClass != classes.idClass) return false;
        if (name != null ? !name.equals(classes.name) : classes.name != null) return false;
        return semester != null ? semester.equals(classes.semester) : classes.semester == null;
    }

    @Override
    public int hashCode() {
        int result = idClass;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Classes{" +
                "idClass=" + idClass +
                ", name='" + name + '\'' +
                ", semester=" + semester +
                '}';
    }
}
