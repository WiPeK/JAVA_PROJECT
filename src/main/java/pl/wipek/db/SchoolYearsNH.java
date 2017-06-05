package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.SchoolYears;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class SchoolYearsNH implements Serializable {

    private Action action;

    private Integer idSchoolYear = 0;

    private String name = "";

    private Date startDate;

    private Date endDate;

    private Set<SemestersNH> semesters = new HashSet<>(0);

    public SchoolYearsNH(SchoolYears schoolYears) {
        this.idSchoolYear = schoolYears.getIdSchoolYear();
        this.name = schoolYears.getName();
        this.startDate = schoolYears.getStartDate();
        this.endDate = schoolYears.getEndDate();
    }

    public SchoolYearsNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Integer getIdSchoolYear() {
        return idSchoolYear;
    }

    public void setIdSchoolYear(Integer idSchoolYear) {
        this.idSchoolYear = idSchoolYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
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

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Set<SemestersNH> getSemesters() {
        return semesters;
    }

    public void setSemesters(Set<SemestersNH> semesters) {
        this.semesters = semesters;
    }

    @Override
    public String toString() {
        return "SchoolYearsNH{" +
                "idSchoolYear=" + idSchoolYear +
                ", name='" + name + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolYearsNH)) return false;

        SchoolYearsNH that = (SchoolYearsNH) o;

        if (idSchoolYear != null ? !idSchoolYear.equals(that.idSchoolYear) : that.idSchoolYear != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
    }

    @Override
    public int hashCode() {
        int result = idSchoolYear != null ? idSchoolYear.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }
}
