package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Grades;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class GradesNH implements Serializable{

    private Action action;

    private int idGrade;

    private Double endGrade;

    private StudentsNH student;

    private CarriedSubjectsNH carriedSubjects;

    private Set<PartialGradesNH> partialGrades = new HashSet<>(0);

    public GradesNH(Grades grades) {
        this.idGrade = grades.getIdGrade();
        this.endGrade = grades.getEndGrade();
        this.student = new StudentsNH(grades.getStudent());
        this.carriedSubjects = new CarriedSubjectsNH(grades.getCarriedSubjects());
    }

    public GradesNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdGrade() {
        return idGrade;
    }

    public void setIdGrade(int idGrade) {
        this.idGrade = idGrade;
    }

    public Double getEndGrade() {
        return endGrade;
    }

    public void setEndGrade(Double endGrade) {
        this.endGrade = endGrade;
    }

    public StudentsNH getStudent() {
        return student;
    }

    public void setStudent(StudentsNH student) {
        this.student = student;
    }

    public CarriedSubjectsNH getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setCarriedSubjects(CarriedSubjectsNH carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    public Set<PartialGradesNH> getPartialGrades() {
        return partialGrades;
    }

    public void setPartialGrades(Set<PartialGradesNH> partialGrades) {
        this.partialGrades = partialGrades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GradesNH)) return false;

        GradesNH gradesNH = (GradesNH) o;

        if (idGrade != gradesNH.idGrade) return false;
        return endGrade != null ? endGrade.equals(gradesNH.endGrade) : gradesNH.endGrade == null;
    }

    @Override
    public int hashCode() {
        int result = idGrade;
        result = 31 * result + (endGrade != null ? endGrade.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GradesNH{" +
                "idGrade=" + idGrade +
                ", endGrade=" + endGrade +
                '}';
    }
}
