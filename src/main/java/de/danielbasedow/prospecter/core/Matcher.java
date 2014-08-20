package de.danielbasedow.prospecter.core;

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

    public void addHits(List<QueryPosting> postings) {
        for (QueryPosting posting : postings) {
            addHit(posting);
        }
    }

    private void addHit(QueryPosting posting) {
        BitSet bits;
        if (hits.containsKey(posting.getQueryId())) {
            bits = hits.get(posting.getQueryId());
        } else {
            bits = new BitSet();
            hits.put(posting.getQueryId(), bits);
        }
        bits.set(posting.getQueryBit());
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
