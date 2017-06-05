package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Subjects;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class SubjectsNH implements Serializable {

    private Action action;

    private int idSubject;

    private String name;

    private Set<CarriedSubjectsNH> carriedSubjects = new HashSet<>(0);

    public SubjectsNH(Subjects subjects) {
        this.idSubject = subjects.getIdSubject();
        this.name = subjects.getName();
    }

    public SubjectsNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdSubject() {
        return idSubject;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<CarriedSubjectsNH> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setCarriedSubjects(Set<CarriedSubjectsNH> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubjectsNH)) return false;

        SubjectsNH that = (SubjectsNH) o;

        if (idSubject != that.idSubject) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = idSubject;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubjectsNH{" +
                "idSubject=" + idSubject +
                ", name='" + name + '\'' +
                '}';
    }
}
