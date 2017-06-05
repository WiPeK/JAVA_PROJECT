package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.PartialGrades;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class PartialGradesNH implements Serializable {

    private Action action;

    private int idGradesList;

    private GradesNH grades;

    private Double grade;

    private Date date;

    public PartialGradesNH(PartialGrades partialGrades) {
        this.idGradesList = partialGrades.getIdGradesList();
        this.grades = new GradesNH(partialGrades.getGrades());
        this.grade = partialGrades.getGrade();
        this.date = partialGrades.getDate();
    }

    public PartialGradesNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdGradesList() {
        return idGradesList;
    }

    public void setIdGradesList(int idGradesList) {
        this.idGradesList = idGradesList;
    }

    public GradesNH getGrades() {
        return grades;
    }

    public void setGrades(GradesNH grades) {
        this.grades = grades;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PartialGradesNH)) return false;

        PartialGradesNH that = (PartialGradesNH) o;

        if (idGradesList != that.idGradesList) return false;
        if (grades != null ? !grades.equals(that.grades) : that.grades != null) return false;
        if (grade != null ? !grade.equals(that.grade) : that.grade != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result = idGradesList;
        result = 31 * result + (grades != null ? grades.hashCode() : 0);
        result = 31 * result + (grade != null ? grade.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PartialGradesNH{" +
                "idGradesList=" + idGradesList +
                ", grades=" + grades +
                ", grade=" + grade +
                ", date=" + date +
                '}';
    }
}
