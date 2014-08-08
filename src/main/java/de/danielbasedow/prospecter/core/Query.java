package de.danielbasedow.prospecter.core;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class Query {
    protected Long queryId;
    protected BitSet mask;
    protected List<Condition> conditions;

    public Long getQueryId() {
        return queryId;
    }

    public Query(Long queryId, List<Condition> conditions) {
        this.conditions = conditions;
        this.queryId = queryId;

        mask = new BitSet(conditions.size());
        mask.set(0, conditions.size(), true); //all bits set to 1
    }

    public HashMap<Condition, QueryPosting> getPostings() {
        HashMap<Condition, QueryPosting> postings = new HashMap<Condition, QueryPosting>();
        short bit = 0;
        for (Condition condition : conditions) {
            postings.put(condition, new QueryPosting(queryId, bit));
            bit++;
        }
        return postings;
    }

    public boolean testBits(BitSet hits) {
        return mask.equals(hits);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

}
