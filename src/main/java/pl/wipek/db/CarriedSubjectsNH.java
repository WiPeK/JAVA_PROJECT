package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.CarriedSubjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class CarriedSubjectsNH implements Serializable {

    private Action action;

    /**
     * contains id of carried subject
     */
    private int idCarriedSubject;

    /**
     * contains id of teacher who teach subject
     */
    private TeachersNH teacher;

    /**
     * contains id of class which has classes
     */
    private ClassesNH classes;

    /**
     * contains id of semester in which classes will be carried
     */
    private SemestersNH semester;

    /**
     * contains id of subject which will be teached
     */
    private SubjectsNH subject;

    /**
     * contains set of grades equivalent to carried subject
     */
    private Set<GradesNH> grades = new HashSet<>(0);

    public CarriedSubjectsNH(CarriedSubjects carriedSubjects) {
        this.idCarriedSubject = carriedSubjects.getIdCarriedSubject();
        this.teacher = new TeachersNH(carriedSubjects.getTeacher());
        this.classes = new ClassesNH(carriedSubjects.getClasses());
        this.semester = new SemestersNH(carriedSubjects.getSemester());
        this.subject = new SubjectsNH(carriedSubjects.getSubject());
    }

    public CarriedSubjectsNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdCarriedSubject() {
        return idCarriedSubject;
    }

    public void setIdCarriedSubject(int idCarriedSubject) {
        this.idCarriedSubject = idCarriedSubject;
    }

    public TeachersNH getTeacher() {
        return teacher;
    }

    public void setTeacher(TeachersNH teacher) {
        this.teacher = teacher;
    }

    public ClassesNH getClasses() {
        return classes;
    }

    public void setClasses(ClassesNH classes) {
        this.classes = classes;
    }

    public SemestersNH getSemester() {
        return semester;
    }

    public void setSemester(SemestersNH semester) {
        this.semester = semester;
    }

    public SubjectsNH getSubject() {
        return subject;
    }

    public void setSubject(SubjectsNH subject) {
        this.subject = subject;
    }

    public Set<GradesNH> getGrades() {
        return grades;
    }

    public void setGrades(Set<GradesNH> grades) {
        this.grades = grades;
    }

    @Override
    public String toString() {
        return "CarriedSubjectsNH{" +
                "idCarriedSubject=" + idCarriedSubject +
                ", teacher=" + teacher +
                ", classes=" + classes +
                ", semester=" + semester +
                ", subject=" + subject +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarriedSubjectsNH)) return false;

        CarriedSubjectsNH that = (CarriedSubjectsNH) o;

        return idCarriedSubject == that.idCarriedSubject;
    }

    @Override
    public int hashCode() {
        return idCarriedSubject;
    }
}
