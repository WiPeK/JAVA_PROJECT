package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Substitutes;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class SubstitutesNH implements Serializable {

    private Action action;

    private int idSubstitute;

    private String body;

    private Date date;

    private AdminsNH admin;

    public SubstitutesNH(Substitutes substitutes) {
        this.idSubstitute = substitutes.getIdSubstitute();
        this.body = substitutes.getBody();
        this.date = substitutes.getDate();
        this.admin = new AdminsNH(substitutes.getAdmin());
    }

    public SubstitutesNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdSubstitute() {
        return idSubstitute;
    }

    public void setIdSubstitute(int idSubstitute) {
        this.idSubstitute = idSubstitute;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public AdminsNH getAdmin() {
        return admin;
    }

    public void setAdmin(AdminsNH admin) {
        this.admin = admin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubstitutesNH)) return false;

        SubstitutesNH that = (SubstitutesNH) o;

        if (idSubstitute != that.idSubstitute) return false;
        if (body != null ? !body.equals(that.body) : that.body != null) return false;
        return date != null ? date.equals(that.date) : that.date == null;
    }

    @Override
    public int hashCode() {
        int result = idSubstitute;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SubstitutesNH{" +
                "idSubstitute=" + idSubstitute +
                ", body='" + body + '\'' +
                ", date=" + date +
                '}';
    }
}
