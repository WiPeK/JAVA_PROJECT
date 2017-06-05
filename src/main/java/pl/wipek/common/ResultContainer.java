package pl.wipek.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Created by Krszysztof Adamczyk on 04.05.2017.
 */
public class ResultContainer implements Serializable {

    private List result = new ArrayList<>(0);

    public List getResult() {
        return result;
    }

    public void setResult(List result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultContainer{" +
                "result=" + result +
                '}';
    }
}
