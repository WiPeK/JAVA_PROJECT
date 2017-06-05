package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.AdminsNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Created by Krzysztof Adamczyk on 15.04.2017.
 * Managing ADMINS Entity
 * Admins is equivalent to table ADMINS in database
 */
@Entity
@Table(name = "ADMINS")
public class Admins implements Serializable {

    /**
     * contains object of Action
     * @see Action
     */
    @Transient
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
     * @see Users
     */
    private Users user;

    /**
     * contains set of all substitutes created by admin
     * @see Substitutes
     */
    private Set<Substitutes> substitutes = new HashSet<Substitutes>(0);

    /**
     * contains set of all classifieds created by admin
     * @see Classifieds
     */
    private Set<Classifieds> classifieds = new HashSet<Classifieds>(0);

    public Admins(AdminsNH adminsNH) {
        this.idAdmin = adminsNH.getIdAdmin();
        this.title = adminsNH.getTitle();
        this.user = new Users(adminsNH.getUser());
    }

    /**
     * get value of admin id
     * @return int with admin id value
     */
    @Id
    @SequenceGenerator(name="admins_seq", sequenceName = "ADMINS_SEQ", initialValue = 10, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "admins_seq")
    @Column(name = "ID_ADMIN")
    public int getIdAdmin() {
        return idAdmin;
    }

    /**
     * get Users object equivalent to Admin user id to Users user id
     * cannot be null
     * @return Users object or null
     */
    @OneToOne
    @JoinColumn(name = "ID_USER", nullable = false)
    public Users getUser() {
        return user;
    }

    /**
     * set Users object equivalent to Admin
     * @param user with Users object
     */
    public void setUser(Users user) {
        this.user = user;
    }

    /**
     * get value of admin title contains academin degree value
     * @return String with admin academic degree value
     */
    @Column(name = "TITLE")
    public String getTitle() {
        return title;
    }

    /**
     * set value of admin academic degree title
     * @param title value of admin academic degree title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * get Substitutes created by admin
     * @see Substitutes
     * @return set with Substitutes created by admin
     */
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Substitutes> getSubstitutes() {
        return substitutes;
    }

    /**
     * set admin's created Substitutes
     * @see Substitutes
     * @param substitutes set with Substitutes created by admin
     */
    public void setSubstitutes(Set<Substitutes> substitutes) {
        this.substitutes = substitutes;
    }

    /**
     * get Classifieds created by admin
     * @return set with Classifieds created by admin
     */
    @OneToMany(mappedBy = "admin", cascade = CascadeType.ALL, orphanRemoval = true)
    public Set<Classifieds> getClassifieds() {
        return classifieds;
    }

    /**
     * set admin's created Classifieds
     * @param classifieds set with Classifieds created by admin
     */
    public void setClassifieds(Set<Classifieds> classifieds) {
        this.classifieds = classifieds;
    }

    /**
     * set admin id
     * @param idAdmin admin id
     */
    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    /**
     * get Action object value
     * @return Action object
     */
    @Transient
    public Action getAction() {
        return action;
    }

    /**
     * set Action object value
     * @param action Action object
     */
    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    /**
     * initializing Admins with Admin title and Users object
     * @param title admin title
     * @param user admin User
     */
    public Admins(String title, Users user) {
        this.title = title;
        this.user = user;
    }

    /**
     * initializing Admins with all components
     * @param idAdmin id admin
     * @param title admin title
     * @param user admin Users
     * @param substitutes admins Substitutes
     * @param classifieds admins Classifieds
     */
    public Admins(int idAdmin, String title, Users user, Set<Substitutes> substitutes, Set<Classifieds> classifieds) {
        this.action = action;
        this.idAdmin = idAdmin;
        this.title = title;
        this.user = user;
        this.substitutes = substitutes;
        this.classifieds = classifieds;
    }

    /**
     * default constructor
     */
    public Admins() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Admins)) return false;

        Admins admins = (Admins) o;

        if (idAdmin != admins.idAdmin) return false;
        if (title != null ? !title.equals(admins.title) : admins.title != null) return false;
        if (!user.equals(admins.user)) return false;
        if (substitutes != null ? !substitutes.equals(admins.substitutes) : admins.substitutes != null) return false;
        return classifieds != null ? classifieds.equals(admins.classifieds) : admins.classifieds == null;
    }

    @Override
    public int hashCode() {
        int result = idAdmin;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Admins{" +
                "idAdmin=" + idAdmin +
                ", title='" + title + '\'' +
                ", user=" + user +
                '}';
    }
}
