package pl.wipek.client;

import pl.wipek.common.Action;
import pl.wipek.common.ResultContainer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Krszysztof Adamczyk on 03.05.2017.
 * managing hibernate relation on client side
 * fetch eager is too slow so client must request to server with every relation
 */
public class RelationHelper {

    /**
     * Controller object to access to client
     */
    private Client client;

    /**
     * initialize with Controller object
     * @param client Controller
     */
    public RelationHelper(Client client) {
        this.client = client;
    }

    /**
     * Get related object from server
     * @param object
     * @return object
     */
    public Object getRelated(Object object) {
        Object tmpObject = this.client.requestServer(object);
        if(tmpObject != null) {
            object = tmpObject;
        }
        return object;
    }

    /**
     * Get set of objects
     * @param action action contains action name and query
     * @return Set with objects
     */
    public Set<Object> getAllAsSet(Action action) {
        ResultContainer resultContainer = (ResultContainer)this.client.requestServer(action);
        Set<Object> result = new HashSet<>();
        result.addAll(resultContainer.getResult());
        return result;
    }

    /**
     * Get list of objects
     * @param action action contains action name and query
     * @return Set with objects
     */
    public List getAll(Action action) {
        ResultContainer resultContainer = (ResultContainer)this.client.requestServer(action);
        return resultContainer.getResult();
    }

}
