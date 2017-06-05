package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.PartialGradesNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "PARTIAL_GRADES")
public class PartialGrades implements Serializable {

    @Transient
    private Action action;

    private int idGradesList;

    private Grades grades;

    private Double grade;

    private Date date;

    public PartialGrades(PartialGradesNH partialGradesNH) {
        this.idGradesList = partialGradesNH.getIdGradesList();
        this.grades = new Grades(partialGradesNH.getGrades());
        this.grade = partialGradesNH.getGrade();
        this.date = partialGradesNH.getDate();
    }

    @Id
    @SequenceGenerator(name="partial_grades_seq", sequenceName = "PARTIAL_GRADES_SEQ", initialValue = 40000, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partial_grades_seq")
    @Column(name = "ID_GRADES_LIST")
    public int getIdGradesList() {
        return idGradesList;
    }

    @ManyToOne
    @JoinColumn(name = "ID_GRADE", nullable = false)
    public Grades getGrades() {
        return grades;
    }

    @Column(name = "GRADE")
    public Double getGrade() {
        return grade;
    }

    @Column(name = "PARTIAL_DATE")
    public Date getDate() {
        return date;
    }

    public void setGrades(Grades grades) {
        this.grades = grades;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setIdGradesList(int idGradesList) {
        this.idGradesList = idGradesList;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public PartialGrades(Grades grades, Double grade, Date date) {
        this.grades = grades;
        this.grade = grade;
        this.date = date;
    }

    public PartialGrades() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartialGrades)) return false;

        PartialGrades that = (PartialGrades) o;

        if (idGradesList != that.idGradesList) return false;
        if (grade != that.grade) return false;
        if (!grades.equals(that.grades)) return false;
        return date.equals(that.date);
    }

    @Override
    public int hashCode() {
        int result = idGradesList;
        result = 31 * result + grades.hashCode();
        result = 31 * result + grade.intValue();
        result = 31 * result + date.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PartialGrades{" +
                "idGradesList=" + idGradesList +
                ", grades=" + grades +
                ", grade=" + grade +
                ", date=" + date +
                '}';
    }
}
