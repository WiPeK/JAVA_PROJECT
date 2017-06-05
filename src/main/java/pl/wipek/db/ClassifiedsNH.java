package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Classifieds;

import java.io.Serializable;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class ClassifiedsNH implements Serializable {

    private Action action;

    private int idClassifieds;

    private String body;

    private AdminsNH admin;

    public ClassifiedsNH(Classifieds classifieds) {
        this.idClassifieds = classifieds.getIdClassifieds();
        this.body = classifieds.getBody();
        this.admin = new AdminsNH(classifieds.getAdmin());
    }

    public ClassifiedsNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdClassifieds() {
        return idClassifieds;
    }

    public void setIdClassifieds(int idClassifieds) {
        this.idClassifieds = idClassifieds;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public AdminsNH getAdmin() {
        return admin;
    }

    public void setAdmin(AdminsNH admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "ClassifiedsNH{" +
                "idClassifieds=" + idClassifieds +
                ", body='" + body + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassifiedsNH)) return false;

        ClassifiedsNH that = (ClassifiedsNH) o;

        if (idClassifieds != that.idClassifieds) return false;
        return body != null ? body.equals(that.body) : that.body == null;
    }

    @Override
    public int hashCode() {
        int result = idClassifieds;
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }
}
