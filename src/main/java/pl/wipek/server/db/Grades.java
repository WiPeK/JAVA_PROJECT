package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.GradesNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "GRADES")
public class Grades implements Serializable {

    @Transient
    private Action action;

    private int idGrade;

    private Double endGrade;

    private Students student;

    private CarriedSubjects carriedSubjects;

    private Set<PartialGrades> partialGrades = new HashSet<>(0);

    public Grades(GradesNH gradesNH) {
        this.idGrade = gradesNH.getIdGrade();
        this.endGrade = gradesNH.getEndGrade();
        this.student = new Students(gradesNH.getStudent());
        this.carriedSubjects = new CarriedSubjects(gradesNH.getCarriedSubjects());
    }

    @Id
    @SequenceGenerator(name="grades_seq", sequenceName = "GRADES_SEQ", initialValue = 3800, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "grades_seq")
    @Column(name = "ID_GRADE")
    public int getIdGrade() {
        return idGrade;
    }

    @Column(name = "END_GRADE", nullable = true)
    public Double getEndGrade() {
        return endGrade;
    }

    @ManyToOne
    @JoinColumn(name = "ID_STUDENT", nullable = false)
    public Students getStudent() {
        return student;
    }

    @OneToMany(mappedBy = "grades")
    public Set<PartialGrades> getPartialGrades() {
        return partialGrades;
    }

    public void setEndGrade(Double endGrade) {
        this.endGrade = endGrade;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    public void setIdGrade(int idGrade) {
        this.idGrade = idGrade;
    }

    public void setPartialGrades(Set<PartialGrades> partialGrades) {
        this.partialGrades = partialGrades;
    }

    public void setCarriedSubjects(CarriedSubjects carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    @ManyToOne
    @JoinColumn(name = "ID_CARRIED_SUBJECT", nullable = false)
    public CarriedSubjects getCarriedSubjects() {
        return carriedSubjects;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public Grades() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grades)) return false;

        Grades grades = (Grades) o;

        if (idGrade != grades.idGrade) return false;
        if (endGrade != null ? !endGrade.equals(grades.endGrade) : grades.endGrade != null) return false;
        if (!student.equals(grades.student)) return false;
        if (!carriedSubjects.equals(grades.carriedSubjects)) return false;
        return partialGrades != null ? partialGrades.equals(grades.partialGrades) : grades.partialGrades == null;
    }

    @Override
    public int hashCode() {
        int result = idGrade;
        result = 31 * result + (endGrade != null ? endGrade.hashCode() : 0);
        result = 31 * result + (student != null ? student.hashCode() : 0);
        result = 31 * result + (carriedSubjects != null ? carriedSubjects.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Grades{" +
                "idGrade=" + idGrade +
                ", endGrade=" + endGrade +
                ", student=" + student +
                ", carriedSubjects=" + carriedSubjects +
                ", partialGrades=" + partialGrades +
                '}';
    }
}
