package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.ClassifiedsNH;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "CLASSIFIEDS")
public class Classifieds implements Serializable  {

    @Transient
    private Action action;

    private int idClassifieds;

    private String body;

    private Admins admin;

    public Classifieds(ClassifiedsNH classifiedsNH) {
        this.idClassifieds = classifiedsNH.getIdClassifieds();
        this.body = classifiedsNH.getBody();
        this.admin = new Admins(classifiedsNH.getAdmin());
    }

    @Id
    @SequenceGenerator(name="classifieds_seq", sequenceName = "CLASSIFIEDS_SEQ", initialValue = 55, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "classifieds_seq")
    @Column(name = "ID_CLASSIFIED")
    public int getIdClassifieds() {
        return idClassifieds;
    }

    @Column(name = "BODY")
    public String getBody() {
        return body;
    }

    @ManyToOne
    @JoinColumn(name = "ID_ADMIN")
    public Admins getAdmin() {
        return admin;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setAdmin(Admins admin) {
        this.admin = admin;
    }

    public void setIdClassifieds(int idClassifieds) {
        this.idClassifieds = idClassifieds;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public Classifieds(String body, Admins admin) {
        this.body = body;
        this.admin = admin;
    }

    public Classifieds() {
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Classifieds)) return false;

        Classifieds that = (Classifieds) o;

        if (idClassifieds != that.idClassifieds) return false;
        if (!body.equals(that.body)) return false;
        return admin.equals(that.admin);
    }

    @Override
    public int hashCode() {
        return idClassifieds;
    }

    @Override
    public String toString() {
        return "Classifieds{" +
                "idClassifieds=" + idClassifieds +
                ", body='" + body + '\'' +
                ", admin=" + admin +
                '}';
    }
}
