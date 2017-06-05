package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.SubstitutesNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */
@Entity
@Table(name = "SUBSTITUTES")
public class Substitutes implements Serializable {

    @Transient
    private Action action;

    private int idSubstitute;

    private String body;

    private Date date;

    private Admins admin;

    public Substitutes(SubstitutesNH substitutesNH) {
        this.idSubstitute = substitutesNH.getIdSubstitute();
        this.body = substitutesNH.getBody();
        this.date = substitutesNH.getDate();
        this.admin = new Admins(substitutesNH.getAdmin());
    }

    @Id
    @SequenceGenerator(name="substitutes_seq", sequenceName = "SUBSTITUTES_SEQ", initialValue = 55, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "substitutes_seq")
    @Column(name = "ID_SUBSTITUTE")
    public int getIdSubstitute() {
        return idSubstitute;
    }

    @Column(name = "BODY")
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Column(name = "SUBSTITUTE_DATE")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @ManyToOne
    @JoinColumn(name = "ID_ADMIN", nullable = false)
    public Admins getAdmin() {
        return admin;
    }

    public void setAdmin(Admins admin) {
        this.admin = admin;
    }

    public void setIdSubstitute(int idSubstitute) {
        this.idSubstitute = idSubstitute;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public Substitutes(String body, Date date, Admins admin) {
        this.body = body;
        this.date = date;
        this.admin = admin;
    }

    public Substitutes() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Substitutes)) return false;

        Substitutes that = (Substitutes) o;

        if (idSubstitute != that.idSubstitute) return false;
        if (!body.equals(that.body)) return false;
        return date.equals(that.date) && admin.equals(that.admin);
    }

    @Override
    public int hashCode() {
        int result = idSubstitute;
        result = 31 * result + body.hashCode();
        result = 31 * result + date.hashCode();
        result = 31 * result + admin.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Substitutes{" +
                "idSubstitute=" + idSubstitute +
                ", body='" + body + '\'' +
                ", date=" + date +
                ", admin=" + admin +
                '}';
    }
}
