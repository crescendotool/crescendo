package org.destecs.core.dcl;

import java.util.List;
import java.util.ArrayList;

public class ResultSet {
    List<List<Object>> results = new ArrayList<List<Object>>();

    public void add(List<Object> row) {
        results.add(row);
    }
}
