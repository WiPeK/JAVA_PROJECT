package pl.wipek.server.db;

import pl.wipek.common.Action;
import pl.wipek.common.ResultContainer;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

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

}
