package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.SchoolYearsNH;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "SCHOOL_YEARS")
public class SchoolYears implements Serializable {

    @Transient
    private Action action;

    private Integer idSchoolYear = 0;

    private String name;

    private Date startDate;

    private Date endDate;

    private Set<Semesters> semesters = new HashSet<>(0);

    public SchoolYears(SchoolYearsNH schoolYearsNH) {
        this.idSchoolYear = schoolYearsNH.getIdSchoolYear();
        this.name = schoolYearsNH.getName();
        this.startDate = schoolYearsNH.getStartDate();
        this.endDate = schoolYearsNH.getEndDate();
    }

    @Id
    @SequenceGenerator(name="school_years_seq", sequenceName = "SCHOOL_YEARS_SEQ", initialValue = 10, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "school_years_seq")
    @Column(name = "ID_SCHOOL_YEAR")
    public Integer getIdSchoolYear() {
        return idSchoolYear;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    @OneToMany(mappedBy = "schoolYear", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Semesters> getSemesters() {
        return semesters;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setIdSchoolYear(int idSchoolYear) {
        this.idSchoolYear = idSchoolYear;
    }

    public void setSemesters(Set<Semesters> semesters) {
        this.semesters = semesters;
    }

    @Transient
    public LocalDate getStartDateAsLocalDate() {
        return this.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Transient
    public void setStartDateFromLocalDate(LocalDate localDate) {
        this.startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Transient
    public LocalDate getEndDateAsLocalDate() {
        return this.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Transient
    public void setEndDateFromLocalDate(LocalDate localDate) {
        this.endDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public SchoolYears(String name, Date startDate, Date endDate) {
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public SchoolYears() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SchoolYears)) return false;

        SchoolYears that = (SchoolYears) o;

        if (idSchoolYear != that.idSchoolYear) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (startDate != null ? !startDate.equals(that.startDate) : that.startDate != null) return false;
        return endDate != null ? endDate.equals(that.endDate) : that.endDate == null;
    }

    @Override
    public int hashCode() {
        int result = (idSchoolYear != null ? idSchoolYear : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SchoolYears{" +
                "idSchoolYear=" + (idSchoolYear != null ? idSchoolYear : "") +
                ", name='" + (name != null ? name : "") + '\'' +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                '}';
    }
}
