package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.TeachersNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "TEACHERS")
public class Teachers implements Serializable {

    @Transient
    private Action action;

    private int idTeacher;

    private Users user;

    private String title;

    private Set<CarriedSubjects> carriedSubjects = new HashSet<CarriedSubjects>(0);

    public Teachers(TeachersNH teachersNH) {
        this.idTeacher = teachersNH.getIdTeacher();
        this.user = new Users(teachersNH.getUser());
        this.title = teachersNH.getTitle();
    }

    @Id
    @SequenceGenerator(name="teachers_seq", sequenceName = "TEACHERS_SEQ", initialValue = 55, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "teachers_seq")
    @Column(name = "ID_TEACHER")
    public int getIdTeacher() {
        return idTeacher;
    }

    @OneToOne
    @JoinColumn(name = "ID_USER", nullable = true)
    public Users getUser() {
        return user;
    }

    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    @OneToMany(mappedBy = "teacher", fetch = FetchType.LAZY)
    public Set<CarriedSubjects> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIdTeacher(int idTeacher) {
        this.idTeacher = idTeacher;
    }

    public void setCarriedSubjects(Set<CarriedSubjects> carriedSubjects) {
        this.carriedSubjects = carriedSubjects;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public Teachers(Users user, String title) {
        this.user = user;
        this.title = title;
    }

    public Teachers() {
    }

    @Override
    public String toString() {
        return "Teachers{" +
                "idTeacher=" + idTeacher +
                ", user=" + user +
                ", title='" + title + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Teachers)) return false;

        Teachers teachers = (Teachers) o;

        if (idTeacher != teachers.idTeacher) return false;
        if (!user.equals(teachers.user)) return false;
        if (title != null ? !title.equals(teachers.title) : teachers.title != null) return false;
        return carriedSubjects != null ? carriedSubjects.equals(teachers.carriedSubjects) : teachers.carriedSubjects == null;
    }

    @Override
    public int hashCode() {
        int result = idTeacher;
        result = 31 * result + (user != null ? user.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
