package de.danielbasedow.prospecter.core.query;

import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.BitSet;

public class QueryManager {
    protected final TIntIntHashMap bitCounts = new TIntIntHashMap();
    protected final TIntObjectHashMap<QueryNegativeCounter> negativeCounts = new TIntObjectHashMap<QueryNegativeCounter>();

    public void addQuery(Query query) {
        bitCounts.put(query.getQueryId(), query.getBits());
        if (query.hasNegatives()) {
            negativeCounts.put(query.getQueryId(), query.getNegativeMask());
        }

    }

    public BitSet getMask(int queryId) {
        BitSet set = new BitSet();
        set.set(0, bitCounts.get(queryId), true);
        return set;
    }

    public void deleteQuery(int queryId) {
        bitCounts.remove(queryId);
    }

    public QueryNegativeCounter getQueryNegativeCounter(int queryId) {
        return negativeCounts.get(queryId);
    }
}
