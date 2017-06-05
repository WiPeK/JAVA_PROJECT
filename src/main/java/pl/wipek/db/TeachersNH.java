package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Teachers;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class TeachersNH implements Serializable {
    private Action action;

    private int idTeacher;

    private UsersNH user;

    private String title;

    private Set<CarriedSubjectsNH> carriedSubjects = new HashSet<>(0);

    public TeachersNH(Teachers teachers) {
        this.idTeacher = teachers.getIdTeacher();
        this.user = new UsersNH(teachers.getUser());
        this.title = teachers.getTitle();
    }

    public TeachersNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdTeacher() {
        return idTeacher;
    }

    public void setIdTeacher(int idTeacher) {
        this.idTeacher = idTeacher;
    }

    public UsersNH getUser() {
        return user;
    }

    public void setUser(UsersNH user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<CarriedSubjectsNH> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setCarriedSubjects(Set<CarriedSubjectsNH> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    @Override
    public String toString() {
        return "TeachersNH{" +
                "idTeacher=" + idTeacher +
                ", user=" + user +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TeachersNH)) return false;

        TeachersNH that = (TeachersNH) o;

        if (idTeacher != that.idTeacher) return false;
        return title != null ? title.equals(that.title) : that.title == null;
    }

    @Override
    public int hashCode() {
        int result = idTeacher;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
