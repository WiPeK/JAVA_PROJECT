package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.SemestersNH;

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
@Table(name = "SEMESTERS")
public class Semesters implements Serializable {

    @Transient
    private Action action;

    private int idSemester;

    private Date startDate;

    private Date endDate;

    private SchoolYears schoolYear;

    private Set<CarriedSubjects> carriedSubjects = new HashSet<>(0);

    private Set<Classes> classes = new HashSet<>(0);

    public Semesters(SemestersNH semestersNH) {
        this.idSemester = semestersNH.getIdSemester();
        this.startDate = semestersNH.getStartDate();
        this.endDate = semestersNH.getEndDate();
        this.schoolYear = new SchoolYears(semestersNH.getSchoolYear());
    }

    @Id
    @SequenceGenerator(name="semesters_seq", sequenceName = "SEMESTERS_SEQ", initialValue = 10, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "semesters_seq")
    @Column(name = "ID_SEMESTER")
    public int getIdSemester() {
        return idSemester;
    }

    @Column(name = "START_DATE")
    public Date getStartDate() {
        return startDate;
    }

    @Column(name = "END_DATE")
    public Date getEndDate() {
        return endDate;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_SCHOOL_YEAR", nullable = false)
    public SchoolYears getSchoolYear() {
        return schoolYear;
    }

    @OneToMany(mappedBy = "semester")
    public Set<CarriedSubjects> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setSchoolYear(SchoolYears schoolYear) {
        this.schoolYear = schoolYear;
    }

    public void setIdSemester(int idSemester) {
        this.idSemester = idSemester;
    }

    public void setCarriedSubjects(Set<CarriedSubjects> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
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

    @OneToMany(mappedBy = "semester")
    public Set<Classes> getClasses() {
        return classes;
    }

    public void setClasses(Set<Classes> classes) {
        this.classes = classes;
    }

    public Semesters(Date startDate, Date endDate, SchoolYears schoolYear) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.schoolYear = schoolYear;
    }

    public Semesters() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Semesters)) return false;

        Semesters semesters = (Semesters) o;

        if (idSemester != semesters.idSemester) return false;
        if (startDate != null ? !startDate.equals(semesters.startDate) : semesters.startDate != null) return false;
        if (endDate != null ? !endDate.equals(semesters.endDate) : semesters.endDate != null) return false;
        return schoolYear != null ? schoolYear.equals(semesters.schoolYear) : semesters.schoolYear == null;
    }

    @Override
    public int hashCode() {
        int result = idSemester;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (schoolYear != null ? schoolYear.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Semesters{" +
                "idSemester=" + idSemester +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", schoolYear=" + schoolYear +
                '}';
    }
}