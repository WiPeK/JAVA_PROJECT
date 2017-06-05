package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Semesters;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class SemestersNH implements Serializable {

    private Action action;

    private int idSemester;

    private Date startDate;

    private Date endDate;

    private SchoolYearsNH schoolYear;

    private Set<CarriedSubjectsNH> carriedSubjects = new HashSet<>(0);

    private Set<ClassesNH> classes = new HashSet<>(0);

    public SemestersNH(Semesters semesters) {
        this.idSemester = semesters.getIdSemester();
        this.startDate = semesters.getStartDate();
        this.endDate = semesters.getEndDate();
        this.schoolYear = semesters.getSchoolYear() == null ? null : new SchoolYearsNH(semesters.getSchoolYear());
    }

    public SemestersNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdSemester() {
        return idSemester;
    }

    public void setIdSemester(int idSemester) {
        this.idSemester = idSemester;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public SchoolYearsNH getSchoolYear() {
        return schoolYear;
    }

    public void setSchoolYear(SchoolYearsNH schoolYear) {
        this.schoolYear = schoolYear;
    }

    public Set<CarriedSubjectsNH> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setCarriedSubjects(Set<CarriedSubjectsNH> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    public Set<ClassesNH> getClasses() {
        return classes;
    }

    public void setClasses(Set<ClassesNH> classes) {
        this.classes = classes;
    }

    public LocalDate getStartDateAsLocalDate() {
        return this.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setStartDateFromLocalDate(LocalDate localDate) {
        this.startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public LocalDate getEndDateAsLocalDate() {
        return this.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void setEndDateFromLocalDate(LocalDate localDate) {
        this.endDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Override
    public String toString() {
        return "SemestersNH{" +
                "idSemester=" + idSemester +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SemestersNH)) return false;

        SemestersNH that = (SemestersNH) o;

        if (idSemester != that.idSemester) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
    }

    @Override
    public int hashCode() {
        int result = idSemester;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
}
