package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Admins;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class AdminsNH implements Serializable {
    /**
     * contains object of Action
     * @see Action
     */
    private Action action;

    /**
     * contains value of admin id
     */
    private int idAdmin;

    /**
     * contains value of admin academic degree
     */
    private String title;

    /**
     * contains Object of User equivalent to user id in Users Entity
     * @see UsersNH
     */
    private UsersNH user;

    /**
     * contains set of all substitutes created by admin
     * @see SubstitutesNH
     */
    private Set<SubstitutesNH> substitutes = new HashSet<SubstitutesNH>(0);

    /**
     * contains set of all classifieds created by admin
     * @see ClassifiedsNH
     */
    private Set<ClassifiedsNH> classifieds = new HashSet<ClassifiedsNH>(0);

    public AdminsNH(int idAdmin, String title, UsersNH user, Set<SubstitutesNH> substitutes, Set<ClassifiedsNH> classifieds) {
        this.idAdmin = idAdmin;
        this.title = title;
        this.user = user;
        this.substitutes = substitutes;
        this.classifieds = classifieds;
    }

    public AdminsNH(Admins admins) {
        this.idAdmin = admins.getIdAdmin();
        this.title = admins.getTitle();
        if(admins.getUser() != null) {
            this.user = new UsersNH(admins.getUser());
        }
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public UsersNH getUser() {
        return user;
    }

    public void setUser(UsersNH user) {
        this.user = user;
    }

    public Set<SubstitutesNH> getSubstitutes() {
        return substitutes;
    }

    public void setSubstitutes(Set<SubstitutesNH> substitutes) {
        this.substitutes = substitutes;
    }

    public Set<ClassifiedsNH> getClassifieds() {
        return classifieds;
    }

    public void setClassifieds(Set<ClassifiedsNH> classifieds) {
        this.classifieds = classifieds;
    }

    public AdminsNH() {
    }

    @Override
    public String toString() {
        return "AdminsNH{" +
                "idAdmin=" + idAdmin +
                ", title='" + title + '\'' +
                ", user=" + user +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AdminsNH)) return false;

        AdminsNH adminsNH = (AdminsNH) o;

        if (idAdmin != adminsNH.idAdmin) return false;
        return title != null ? title.equals(adminsNH.title) : adminsNH.title == null;
    }

    @Override
    public int hashCode() {
        int result = idAdmin;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }
}
