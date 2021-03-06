package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.common.ResultContainer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Acer on 15.04.2017.
 */
public class HibernateUtil {

    private final static EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("pl.wipek.database");

    private final static EntityManager entityManager;

    static{
        entityManager = entityManagerFactory.createEntityManager();
    }

    public static ResultContainer getAll(Action action) {
        entityManager.clear();
        ResultContainer resultContainer = new ResultContainer();
        resultContainer.setResult(entityManager.createQuery(action.getQuery()).getResultList());
        return resultContainer;
    }

    public static ResultContainer getAmountStats() {
        entityManager.clear();
        ResultContainer resultContainer = new ResultContainer();
        List result = new ArrayList();
        result.add(entityManager.createQuery("FROM Classes cs").getResultList().size());
        result.add(entityManager.createQuery("FROM Subjects su").getResultList().size());
        result.add(entityManager.createQuery("FROM Admins ad").getResultList().size());
        result.add(entityManager.createQuery("FROM Teachers tc").getResultList().size());
        result.add(entityManager.createQuery("FROM Students st").getResultList().size());
        result.add(entityManager.createQuery("FROM Users u").getResultList().size());
        resultContainer.setResult(result);
        return resultContainer;
    }
}
