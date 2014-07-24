package de.danielbasedow.prospecter.core;

import java.util.BitSet;

public class Query {
    protected Long queryId;
    protected BitSet mask;
    protected Integer[] termIds;

    public Long getQueryId() {
        return queryId;
    }

    public Query(Long queryId, Integer[] termIds) {
        this.termIds = termIds;
        this.queryId = queryId;

        mask = new BitSet(termIds.length);
        mask.set(0, termIds.length - 1, true); //all bits set to 1
    }
}
