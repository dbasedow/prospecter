package de.danielbasedow.prospecter.core.query;

import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.BitSet;

public class QueryManager {
    protected TIntObjectHashMap<BitSet> masks;

    public QueryManager() {
        this.masks = new TIntObjectHashMap<BitSet>();
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
