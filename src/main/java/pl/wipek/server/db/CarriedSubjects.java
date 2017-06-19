package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.CarriedSubjectsNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Created by Krzysztof Adamczyk on 15.04.2017.
 * Managing CarriedSubject Entity
 * CarriedSubjects equals to table CARRIED_SUBJECTS in database
 */
@Entity
@Table(name = "CARRIED_SUBJECTS")
public class CarriedSubjects implements Serializable {

    /**
     * contains object of Action
     * @see Action
     */
    @Transient
    private Action action;

    /**
     * contains id of carried subject
     */
    private int idCarriedSubject;

    /**
     * contains id of teacher who teach subject
     */
    private Teachers teacher;

    /**
     * contains id of class which has classes
     */
    private Classes classes;

    /**
     * contains id of semester in which classes will be carried
     */
    private Semesters semester;

    /**
     * contains id of subject which will be teached
     */
    private Subjects subject;

    /**
     * contains set of grades equivalent to carried subject
     */
    private Set<Grades> grades = new HashSet<>(0);

    public CarriedSubjects(CarriedSubjectsNH carriedSubjectsNH) {
        this.idCarriedSubject = carriedSubjectsNH.getIdCarriedSubject();
        this.teacher = new Teachers(carriedSubjectsNH.getTeacher());
        this.classes = new Classes(carriedSubjectsNH.getClasses());
        this.semester = new Semesters(carriedSubjectsNH.getSemester());
        this.subject = new Subjects(carriedSubjectsNH.getSubject());
    }

    /**
     * get value of carried subjects id
     * @return int with carried subjects id
     */
    @Id
    @SequenceGenerator(name="carried_subjects_seq", sequenceName = "CARRIED_SUBJECTS_SEQ", initialValue = 380, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "carried_subjects_seq")
    @Column(name = "ID_CARRIED_SUBJECT")
    public int getIdCarriedSubject() {
        return idCarriedSubject;
    }

    /**
     * get value of id of teacher who teach subject
     * @return int with teacher id
     */
    @ManyToOne
    @JoinColumn(name = "ID_TEACHER", nullable = false)
    public Teachers getTeacher() {
        return teacher;
    }

    /**
     * get value of id of class which has classes
     * @return int with class id
     */
    @ManyToOne
    @JoinColumn(name = "ID_CLASS", nullable = false)
    public Classes getClasses() {
        return classes;
    }

    /**
     * get value of carried subject semester
     * @return int with semester id
     */
    @ManyToOne
    @JoinColumn(name = "ID_SEMESTER", nullable = false)
    public Semesters getSemester() {
        return semester;
    }

    /**
     * get value if carried subject id
     * @return int with subject id
     */
    @ManyToOne
    @JoinColumn(name = "ID_SUBJECT", nullable = false)
    public Subjects getSubject() {
        return subject;
    }

    /**
     * set Teacher Object
     * @param teacher Teacher
     */
    public void setTeacher(Teachers teacher) {
        this.teacher = teacher;
    }

    /**
     * set Classes object
     * @param classes Classes
     */
    public void setClasses(Classes classes) {
        this.classes = classes;
    }

    /**
     * set Semester object
     * @param semester Semester
     */
    public void setSemester(Semesters semester) {
        this.semester = semester;
    }

    /**
     * set Subject object
     * @param subject Subject object
     */
    public void setSubject(Subjects subject) {
        this.subject = subject;
    }

    /**
     * set value of id carried subject
     * @param idCarriedSubject carried subject id
     */
    public void setIdCarriedSubject(int idCarriedSubject) {
        this.idCarriedSubject = idCarriedSubject;
    }

    /**
     * get Action object value
     * @return Action object
     */
    @Transient
    public Action getAction() {
        return action;
    }

    /**
     * set Action object value
     * @param action Action object
     */
    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * constructor with all fields
     * @param action Action object
     * @param idCarriedSubject int
     * @param teacher Teacher
     * @param classes Classes
     * @param semester Semesters
     * @param subject Subjects
     * @param grades Grades
     */
    public CarriedSubjects(Action action, int idCarriedSubject, Teachers teacher, Classes classes, Semesters semester, Subjects subject, Set<Grades> grades) {
        this.action = action;
        this.idCarriedSubject = idCarriedSubject;
        this.teacher = teacher;
        this.classes = classes;
        this.semester = semester;
        this.subject = subject;
        this.grades = grades;
    }

    /**
     * default constructor
     */
    public CarriedSubjects() {
    }

    /**
     * get set with grades
     * @return set with grades
     */
    @OneToMany(mappedBy = "carriedSubjects")
    public Set<Grades> getGrades() {
        return grades;
    }

    /**
     * set grades set
     * @param grades set with grades
     */
    public void setGrades(Set<Grades> grades) {
        this.grades = grades;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarriedSubjects)) return false;

        CarriedSubjects that = (CarriedSubjects) o;

        if (idCarriedSubject != that.idCarriedSubject) return false;
        if (teacher != null ? !teacher.equals(that.teacher) : that.teacher != null) return false;
        if (classes != null ? !classes.equals(that.classes) : that.classes != null) return false;
        if (semester != null ? !semester.equals(that.semester) : that.semester != null) return false;
        return subject != null ? subject.equals(that.subject) : that.subject == null;
    }

    @Override
    public int hashCode() {
        int result = idCarriedSubject;
        result = 31 * result + (teacher != null ? teacher.hashCode() : 0);
        result = 31 * result + (classes != null ? classes.hashCode() : 0);
        result = 31 * result + (semester != null ? semester.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CarriedSubjects{" +
                "idCarriedSubject=" + idCarriedSubject + '}';
    }

}
