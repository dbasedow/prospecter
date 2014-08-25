package de.danielbasedow.prospecter.core;

import gnu.trove.list.array.TLongArrayList;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * Tracks hits across multiple index fields and applies the bit from the QueryPostings.
 */
public class Matcher {
    protected HashMap<Integer, BitSet> hits;
    protected QueryManager queryManager;

    public Matcher(QueryManager qm) {
        queryManager = qm;
        hits = new HashMap<Integer, BitSet>();
    }

    public void addHits(TLongArrayList postings) {
        for (long posting : postings.toArray()) {
            addHit(posting);
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

    public List<Query> getMatchedQueries() {
        List<Query> results = new ArrayList<Query>();
        for (Integer queryId : hits.keySet()) {
            Query query = queryManager.getQuery(queryId);
            if (query != null && query.testBits(hits.get(queryId))) {
                results.add(query);
            }
        }
        return results;
    }
}
