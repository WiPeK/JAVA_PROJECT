package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.SubjectsNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Acer on 15.04.2017.
 */
@Entity
@Table(name = "SUBJECTS")
public class Subjects implements Serializable {

    @Transient
    private Action action;

    private int idSubject;

    private String name;

    private Set<CarriedSubjects> carriedSubjects = new HashSet<CarriedSubjects>(0);

    public Subjects(SubjectsNH subjectsNH) {
        this.idSubject = subjectsNH.getIdSubject();
        this.name = subjectsNH.getName();
    }

    @Id
    @SequenceGenerator(name="subjects_seq", sequenceName = "SUBJECTS_SEQ", initialValue = 20, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "subjects_seq")
    @Column(name = "ID_SUBJECT")
    public int getIdSubject() {
        return idSubject;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<CarriedSubjects> getCarriedSubjects() {
        return carriedSubjects;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIdSubject(int idSubject) {
        this.idSubject = idSubject;
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

    public Subjects(String name) {
        this.name = name;
    }

    public Subjects() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subjects)) return false;

        Subjects subjects = (Subjects) o;

        if (idSubject != subjects.idSubject) return false;
        if (!name.equals(subjects.name)) return false;
        return carriedSubjects != null ? carriedSubjects.equals(subjects.carriedSubjects) : subjects.carriedSubjects == null;
    }

    @Override
    public int hashCode() {
        int result = idSubject;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Subjects{" +
                "idSubject=" + idSubject +
                ", name='" + name + '\'' +
                '}';
    }
}
