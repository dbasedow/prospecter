package de.danielbasedow.prospecter.core.query;

import gnu.trove.map.hash.TIntIntHashMap;

import java.util.BitSet;

public class QueryManager {
    protected final TIntIntHashMap bitCounts = new TIntIntHashMap();

    public void addQuery(Query query) {
        bitCounts.put(query.getQueryId(), query.getBits());
    }

    public BitSet getMask(int queryId) {
        BitSet set = new BitSet();
        set.set(0, bitCounts.get(queryId), true);
        return set;
    }

    public void deleteQuery(int queryId) {
        bitCounts.remove(queryId);
    }
}
