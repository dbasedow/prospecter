package de.danielbasedow.prospecter.core.query;

import java.util.BitSet;
import java.util.HashMap;

public class QueryManager {
    protected HashMap<Integer, BitSet> masks;

    public QueryManager() {
        this.masks = new HashMap<Integer, BitSet>();
    }

    public void addQuery(Query query) {
        masks.put(query.getQueryId(), query.getMask());
    }

    public BitSet getMask(Integer queryId) {
        return masks.get(queryId);
    }

    public void deleteQuery(Integer queryId) {
        masks.remove(queryId);
    }
}
