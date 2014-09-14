package de.danielbasedow.prospecter.core.query;

import gnu.trove.map.TIntByteMap;
import gnu.trove.map.hash.TIntByteHashMap;

/**
 * Keeps track of how many negated literals are in a disjunction (if the matched negatives at a bit are less than the
 * count kept in this object it means one NOT literal was not found and the clause is true.
 */
public class QueryNegativeCounter {
    private final TIntByteMap bitCounts = new TIntByteHashMap();

    public void add(int bit) {
        bitCounts.adjustOrPutValue(bit, (byte) 1, (byte) 1);
    }
}
