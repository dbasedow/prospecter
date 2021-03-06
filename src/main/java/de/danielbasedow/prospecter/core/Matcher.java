package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.QueryNegativeCounter;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import gnu.trove.list.TLongList;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.procedure.TIntObjectProcedure;
import gnu.trove.procedure.TLongProcedure;

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
        postings.forEach(new TLongProcedure() {
            @Override
            public boolean execute(long posting) {
                addHit(posting);
                return true;
            }
        });
    }

    public void addHit(long posting) {
        int[] unpacked = QueryPosting.unpack(posting);
        int queryId = unpacked[QueryPosting.QUERY_ID_INDEX];

        if (unpacked[QueryPosting.QUERY_NOT_INDEX] == 1) {
            QueryNegativeCounter counter = negativeHits.get(queryId);
            if (counter == null) {
                counter = new QueryNegativeCounter();
                negativeHits.put(queryId, counter);
            }
            counter.add(unpacked[QueryPosting.QUERY_BIT_INDEX]);
        } else {
            BitSet bits = hits.get(queryId);
            if (bits == null) {
                bits = new BitSet();
                hits.put(queryId, bits);
            }
            bits.set(unpacked[QueryPosting.QUERY_BIT_INDEX]);
        }
    }

    public int getPositiveMatchCount() {
        return hits.size();
    }

    public int getNegativeMatchCount() {
        return negativeHits.size();
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
                        if (countMask != null) {
                            QueryNegativeCounter actualCount = negativeHits.get(queryId);
                            if (actualCount == null) {
                                actualCount = new QueryNegativeCounter();
                            }
                            int[] bitPositions = countMask.getBitPositionsToSet(actualCount);
                            if (bitPositions.length > 0) {
                                for (int position : bitPositions) {
                                    bitSet.set(position, true);
                                }
                            }
                            if (mask.equals(bitSet)) {
                                results.add(queryId);
                            }
                        }
                    }
                }
                return true;
            }
        });
        return results;
    }
}
