package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.db.UsersNH;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 */

@Entity
@Table(name = "USERS", uniqueConstraints = {
        @UniqueConstraint(columnNames = "EMAIL"),
        @UniqueConstraint(columnNames = "PESEL")
})
public class Users implements Serializable {

    @Transient
    private Action action;

    private int idUser;

    private String password;

    private String email;

    private String name;

    private String surname;

    private String pesel;

    private Date createDate;

    private Date lastLogIn;

    private Date lastLogOut;

    private Admins admin = null;

    private Teachers teacher = null;

    private Students student = null;

    public Users(UsersNH usersNH) {
        this.idUser = usersNH.getIdUser();
        this.password = usersNH.getPassword();
        this.email = usersNH.getEmail();
        this.name = usersNH.getName();
        this.surname = usersNH.getSurname();
        this.pesel = usersNH.getPesel();
        this.createDate = usersNH.getCreateDate();
        this.lastLogIn = usersNH.getLastLogIn();
        this.lastLogOut = usersNH.getLastLogOut();
    }

    @Id
    @SequenceGenerator(name="users_seq", sequenceName = "USERS_SEQ", initialValue = 1200, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_seq")
    @Column(name = "ID_USER")
    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Column(name = "EMAIL")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "PASSWORD")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "SURNAME")
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Column(name = "PESEL")
    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    @Column(name = "CREATE_DATE")
    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Column(name = "LAST_LOG_IN")
    public Date getLastLogIn() {
        return lastLogIn;
    }

    public void setLastLogIn(Date lastLogIn) {
        this.lastLogIn = lastLogIn;
    }

    @Column(name = "LAST_LOG_OUT")
    public Date getLastLogOut() {
        return lastLogOut;
    }

    public void setLastLogOut(Date lastLogOut) {
        this.lastLogOut = lastLogOut;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public Admins getAdmin() {
        return admin;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public Teachers getTeacher() {
        return teacher;
    }

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    public Students getStudent() {
        return student;
    }

    public void setAdmin(Admins admin) {
        this.admin = admin;
    }

    public void setStudent(Students student) {
        this.student = student;
    }

    public void setTeacher(Teachers teacher) {
        this.teacher = teacher;
    }

    @Transient
    public Action getAction() {
        return action;
    }

    @Transient
    public void setAction(Action action) {
        this.action = action;
    }

    public Users(String password, String email, String name, String surname, String pesel, Date createDate) {
        this.password = password;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.pesel = pesel;
        this.createDate = createDate;
    }

    public Users() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Users)) return false;

        Users users = (Users) o;

        if (idUser != users.idUser) return false;
        if (password != null ? !password.equals(users.password) : users.password != null) return false;
        if (email != null ? !email.equals(users.email) : users.email != null) return false;
        if (name != null ? !name.equals(users.name) : users.name != null) return false;
        if (surname != null ? !surname.equals(users.surname) : users.surname != null) return false;
        return pesel != null ? pesel.equals(users.pesel) : users.pesel == null;
    }

    @Override
    public int hashCode() {
        int result = idUser;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (pesel != null ? pesel.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Users{" +
                "idUser=" + idUser +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", pesel='" + pesel + '\'' +
                '}';
    }
}
