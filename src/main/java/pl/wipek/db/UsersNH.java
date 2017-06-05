package pl.wipek.db;

import pl.wipek.common.Action;
import pl.wipek.server.db.Users;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Krszysztof Adamczyk on 11.05.2017.
 */
public class UsersNH implements Serializable {

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

    private AdminsNH admin;

    private TeachersNH teacher;

    private StudentsNH student;

    private String type = "";

    transient private boolean isInClass = false;

    public UsersNH(Users users) {
        this.idUser = users.getIdUser();
        this.password = users.getPassword();
        this.email = users.getEmail();
        this.name = users.getName();
        this.surname = users.getSurname();
        this.pesel = users.getPesel();
        this.createDate = users.getCreateDate();
        this.lastLogIn = users.getLastLogIn();
        this.lastLogOut = users.getLastLogOut();
        if(users.getStudent() != null) {
            this.type = "Uczeń";
            StudentsNH studentsNH = new StudentsNH();
            studentsNH.setIdStudent(users.getStudent().getIdStudent());
            studentsNH.setUser(this);
            this.student = studentsNH;
        }
        if(users.getTeacher() != null) {
            this.type = "Nauczyciel";
            TeachersNH teachersNH = new TeachersNH();
            teachersNH.setIdTeacher(users.getTeacher().getIdTeacher());
            teachersNH.setTitle(users.getTeacher().getTitle());
            teachersNH.setUser(this);
            this.teacher = teachersNH;
        }
        if(users.getAdmin() != null) {
            this.type = "Administrator";
            AdminsNH adminsNH = new AdminsNH();
            adminsNH.setIdAdmin(users.getAdmin().getIdAdmin());
            adminsNH.setTitle(users.getAdmin().getTitle());
            adminsNH.setUser(this);
            this.admin = adminsNH;
        }
    }

    public UsersNH() {
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPesel() {
        return pesel;
    }

    public void setPesel(String pesel) {
        this.pesel = pesel;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastLogIn() {
        return lastLogIn;
    }

    public void setLastLogIn(Date lastLogIn) {
        this.lastLogIn = lastLogIn;
    }

    public Date getLastLogOut() {
        return lastLogOut;
    }

    public void setLastLogOut(Date lastLogOut) {
        this.lastLogOut = lastLogOut;
    }

    public AdminsNH getAdmin() {
        return admin;
    }

    public void setAdmin(AdminsNH admin) {
        this.admin = admin;
    }

    public TeachersNH getTeacher() {
        return teacher;
    }

    public void setTeacher(TeachersNH teacher) {
        this.teacher = teacher;
    }

    public StudentsNH getStudent() {
        return student;
    }

    public void setStudent(StudentsNH student) {
        this.student = student;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isInClass() {
        return isInClass;
    }

    public void setInClass(boolean inClass) {
        isInClass = inClass;
    }

    @Override
    public String toString() {
        return "UsersNH{" +
                "idUser=" + idUser +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", pesel='" + pesel + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsersNH)) return false;

        UsersNH usersNH = (UsersNH) o;

        if (idUser != usersNH.idUser) return false;
        if (email != null ? !email.equals(usersNH.email) : usersNH.email != null) return false;
        if (name != null ? !name.equals(usersNH.name) : usersNH.name != null) return false;
        if (surname != null ? !surname.equals(usersNH.surname) : usersNH.surname != null) return false;
        return pesel != null ? pesel.equals(usersNH.pesel) : usersNH.pesel == null;
    }

    @Override
    public int hashCode() {
        int result = idUser;
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (pesel != null ? pesel.hashCode() : 0);
        result = 31 * result + (createDate != null ? createDate.hashCode() : 0);
        result = 31 * result + (lastLogIn != null ? lastLogIn.hashCode() : 0);
        result = 31 * result + (lastLogOut != null ? lastLogOut.hashCode() : 0);
        return result;
    }
}
