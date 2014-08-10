package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.index.FullTextIndex;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

/**
 * Tracks hits across multiple index fields and applies the bit from the QueryPostings.
 */
public class Matcher {
    protected HashMap<Long, BitSet> hits;
    protected QueryManager queryManager;

    public Matcher(QueryManager qm) {
        queryManager = qm;
        hits = new HashMap<Long, BitSet>();
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
        for (Long queryId : hits.keySet()) {
            Query query = queryManager.getQuery(queryId);
            if (query.testBits(hits.get(queryId))) {
                results.add(query);
            }
        }
        return results;
    }
}
