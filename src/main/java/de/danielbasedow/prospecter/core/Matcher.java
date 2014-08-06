package de.danielbasedow.prospecter.core;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.index.FullTextIndex;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class Matcher {
    protected HashMap<Long, BitSet> hits;
    protected QueryManager queryManager;

    @Inject
    public Matcher(QueryManager qm) {
        queryManager = qm;
        hits = new HashMap<Long, BitSet>();
    }

    public void collectHits(FullTextIndex index, List<Token> tokens) {
        for (Token token : tokens) {
            List<QueryPosting> postings = index.getQueryPostingsForTermId(token);
            if (postings != null) {
                for (QueryPosting posting : postings) {
                    addHit(posting);
                }
            }
        }
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

    public void printResultStats() {
        System.out.println("Hits: " + Integer.toString(hits.size()));
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
