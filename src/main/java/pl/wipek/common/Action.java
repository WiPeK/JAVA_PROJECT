package pl.wipek.common;

import java.io.Serializable;

/**
 * @author Created by Krzysztof Adamczyk on 21.04.2017.
 * Class manage actions executing in server
 */
public class Action implements Serializable {

    /**
     * String containing command to execute
     */
    private String action;

    /**
     * String containing database query
     */
    private String query;

    /**
     * get value of action
     * @return String with value of action
     */
    public String getAction() {
        return action;
    }

    /**
     * setting value of action
     * @param action String with command to execute
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * initializing object with command
     * @param action action String
     */
    public Action(String action) {
        this.action = action;
    }

    /**
     * get value of action query
     * @return String value of query
     */
    public String getQuery() {
        return query;
    }

    /**
     * set value of query
     * @param query String with query value
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * initializing with command and query
     * @param action String with command
     * @param query query String
     */
    public Action(String action, String query) {
        this.action = action;
        this.query = query;
    }

    @Override
    public String toString() {
        return "Action{" +
                "action='" + action + '\'' +
                ", query='" + query + '\'' +
                '}';
    }
}
