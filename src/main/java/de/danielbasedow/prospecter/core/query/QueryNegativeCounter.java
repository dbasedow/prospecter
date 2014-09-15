package de.danielbasedow.prospecter.core.query;

import gnu.trove.list.TByteList;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TByteArrayList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntByteMap;
import gnu.trove.map.hash.TIntByteHashMap;

import java.util.List;

/**
 * Keeps track of how many negated literals are in a disjunction (if the matched negatives at a bit are less than the
 * count kept in this object it means one NOT literal was not found and the clause is true.
 */
public class QueryNegativeCounter {
    private final TIntByteMap bitCounts = new TIntByteHashMap();

    public void add(int bit) {
        bitCounts.adjustOrPutValue(bit, (byte) 1, (byte) 1);
    }

    public int size() {
        return bitCounts.size();
    }

    public byte getCountAtBit(int bit) {
        return bitCounts.get(bit);
    }

    public int[] getBitPositionsToSet(QueryNegativeCounter actualMatched) {
        TIntList results = new TIntArrayList();

        for (int bitPosition : bitCounts.keys()) {
            if (bitCounts.get(bitPosition) > actualMatched.getCountAtBit(bitPosition)) {
                results.add(bitPosition);
            }
        }

        return results.toArray();
    }
}
