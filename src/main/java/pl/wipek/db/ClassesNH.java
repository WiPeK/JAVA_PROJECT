package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Classes;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class ClassesNH implements Serializable {

    private Action action;

    private int idClass;

    private String name;

    private SemestersNH semester;

    private Set<CarriedSubjectsNH> carriedSubjects = new HashSet<>(0);

    private Set<StudentsClassesNH> studentsClasses = new HashSet<>(0);

    public ClassesNH(Classes classes) {
        this.idClass = classes.getIdClass();
        this.name = classes.getName();
        this.semester = new SemestersNH(classes.getSemester());
    }

    public ClassesNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdClass() {
        return idClass;
    }

    public void setIdClass(int idClass) {
        this.idClass = idClass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SemestersNH getSemester() {
        return semester;
    }

    public void setSemester(SemestersNH semester) {
        this.semester = semester;
    }

    public Set<CarriedSubjectsNH> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setCarriedSubjects(Set<CarriedSubjectsNH> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    public Set<StudentsClassesNH> getStudentsClasses() {
        return studentsClasses;
    }

    public void setStudentsClasses(Set<StudentsClassesNH> studentsClasses) {
        this.studentsClasses = studentsClasses;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassesNH)) return false;

        ClassesNH classesNH = (ClassesNH) o;

        if (idClass != classesNH.idClass) return false;
        return name != null ? name.equals(classesNH.name) : classesNH.name == null;
    }

    @Override
    public int hashCode() {
        int result = idClass;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ClassesNH{" +
                "idClass=" + idClass +
                ", name='" + name + '\'' +
                '}';
    }
}
