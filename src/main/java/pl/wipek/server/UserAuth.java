package pl.wipek.server;

import pl.wipek.helpers.Hasher;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import pl.wipek.db.AdminsNH;
import pl.wipek.db.StudentsNH;
import pl.wipek.db.TeachersNH;
import pl.wipek.db.UsersNH;
import pl.wipek.server.db.Users;

import java.util.Date;

/**
 * Created by Krzysztof Adamczyk on 15.04.2017.
 * Managing user authorization
 */
class UserAuth {

    /**
     * Log4j2 logger using to log events to file
     */
    private static final Logger logger = LogManager.getRootLogger();

    /**
     * @author Krzysztof Adamczyk
     * Logging user to server and returning Users object with all data of user
     * setting user last log in and authorization key with Hasher
     * @param auth Users object with email and passwords
     * @return Object: Users when login passed otherwise null
     */
    static Object tryLogIn(Users auth) {
        Object result = Boolean.FALSE;
        String email = auth.getEmail();
        String password = Hasher.hashSHA512(auth.getPassword());
        try {
            Users user = Router.getEntityManager().createQuery("SELECT u FROM Users u WHERE u.email = :email AND u.password = :password", Users.class)
                    .setParameter("email", email)
                    .setParameter("password", password)
                    .getSingleResult();
            if(user != null) {
                user.setLastLogIn(new Date());
                Router.getEntityManager().persist(user);
                UsersNH usersNH = new UsersNH(user);
                if(user.getStudent() != null) {
                    usersNH.setStudent(new StudentsNH(user.getStudent()));
                }
                else if(user.getTeacher() != null) {
                    usersNH.setTeacher(new TeachersNH(user.getTeacher()));
                }
                else if(user.getAdmin() != null) {
                    usersNH.setAdmin(new AdminsNH(user.getAdmin()));
                }
                result = usersNH;
            } else {
                Router.getEntityManager().getTransaction().rollback();
            }
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
        return result;
    }
//todo remove auth key
    /**
     * @author Krzysztof Adamczyk
     * trying to logging out user
     * setting user last log out and delete authorization key
     * @param user Users object which wants to log out
     * @return Object Boolean true is all passed successfully otherwise Boolean false
     */
    static Object tryLogOut(Users user) {
        Router.getEntityManager().clear();
        Object result = Boolean.FALSE;
        try {
            if(user != null) {
                user.setLastLogOut(new Date());
                Router.getEntityManager().merge(user);
                result = Boolean.TRUE;
            } else {
                Router.getEntityManager().getTransaction().rollback();
            }
        } catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
        }
        return result;
    }
}
