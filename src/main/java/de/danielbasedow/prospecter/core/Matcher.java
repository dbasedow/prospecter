package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.query.Query;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.QueryNegativeCounter;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import gnu.trove.list.TLongList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * Tracks hits across multiple index fields and applies the bit from the QueryPostings.
 */
public class Matcher {
    protected final TIntObjectHashMap<BitSet> hits = new TIntObjectHashMap<BitSet>();
    protected final TIntObjectHashMap<QueryNegativeCounter> negativeHits = new TIntObjectHashMap<QueryNegativeCounter>();

    protected final QueryManager queryManager;

    public Matcher(QueryManager qm) {
        queryManager = qm;
    }

    public void addHits(TLongList postings) {
        for (long posting : postings.toArray()) {
            addHit(posting);
        }
    }

    public void addNegativeHits(TLongList postings) {
        for (long posting : postings.toArray()) {
            addNegativeHit(posting);
        }
    }

    private void addHit(long posting) {
        BitSet bits;
        int[] unpacked = QueryPosting.unpack(posting);

        if (hits.containsKey(unpacked[QueryPosting.QUERY_ID_INDEX])) {
            bits = hits.get(unpacked[QueryPosting.QUERY_ID_INDEX]);
        } else {
            bits = new BitSet();
            hits.put(unpacked[QueryPosting.QUERY_ID_INDEX], bits);
        }
        bits.set(unpacked[QueryPosting.QUERY_BIT_INDEX]);
    }

    private void addNegativeHit(long posting) {
        QueryNegativeCounter counter;
        int[] unpacked = QueryPosting.unpack(posting);

        if (negativeHits.containsKey(unpacked[QueryPosting.QUERY_ID_INDEX])) {
            counter = negativeHits.get(unpacked[QueryPosting.QUERY_ID_INDEX]);
        } else {
            counter = new QueryNegativeCounter();
            negativeHits.put(unpacked[QueryPosting.QUERY_ID_INDEX], counter);
        }
        counter.add(unpacked[QueryPosting.QUERY_BIT_INDEX]);
    }

    public List<Integer> getMatchedQueries() {
        final List<Integer> results = new ArrayList<Integer>();

        hits.forEachEntry(new TIntObjectProcedure<BitSet>() {
            @Override
            public boolean execute(int queryId, BitSet bitSet) {
                BitSet mask = queryManager.getMask(queryId);
                if (mask != null) {
                    if (mask.equals(bitSet)) {
                        results.add(queryId);
                    } else {
                        QueryNegativeCounter countMask = queryManager.getQueryNegativeCounter(queryId);
                        QueryNegativeCounter actualCount = negativeHits.get(queryId);
                        if (actualCount == null) {
                            actualCount = new QueryNegativeCounter();
                        }
                        if (countMask != null) {
                            int[] bitPositions = countMask.getBitPositionsToSet(actualCount);
                            if (bitPositions.length > 0) {
                                for (int position : bitPositions) {
                                    bitSet.set(position, true);
                                }
                            }
                        }
                        if (mask.equals(bitSet)) {
                            results.add(queryId);
                        }
                    }
                }
                return true;
            }
        });
        return results;
    }
}
