package de.danielbasedow.prospecter.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Query {
    protected Long queryId;
    protected BitSet mask;
    protected ArrayList<Integer> termIds;

    public Long getQueryId() {
        return queryId;
    }

    public Query(Long queryId, ArrayList<Integer> termIds) {
        this.termIds = termIds;
        this.queryId = queryId;

        mask = new BitSet(termIds.size());
        mask.set(0, termIds.size(), true); //all bits set to 1
    }

    public HashMap<Integer, QueryPosting> getPostings() {
        HashMap<Integer, QueryPosting> postings = new HashMap<Integer, QueryPosting>();
        short bit = 0;
        for (Integer termId : termIds) {
            postings.put(termId, new QueryPosting(queryId, bit));
            bit++;
        }
        return postings;
    }

    public boolean testBits(BitSet hits) {
        return mask.equals(hits);
    }
}
