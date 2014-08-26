package de.danielbasedow.prospecter.core.query;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * represents a Query (a tuple of queryId, bitmask and a list of conditions)
 */
public class Query {
    protected Integer queryId;
    protected BitSet mask;
    protected List<Condition> conditions;

    public Integer getQueryId() {
        return queryId;
    }

    public Query(Integer queryId, List<Condition> conditions) {
        this.conditions = conditions;
        this.queryId = queryId;

        mask = new BitSet(conditions.size());
        mask.set(0, conditions.size(), true); //all bits set to 1
    }

    /**
     * Get QueryPostings for every Condition
     *
     * @return map of Condition -> QueryPosting
     */
    public Map<Condition, Long> getPostings() {
        Map<Condition, Long> postings = new HashMap<Condition, Long>();
        short bit = 0;
        for (Condition condition : conditions) {
            postings.put(condition, QueryPosting.pack(queryId, bit));
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
