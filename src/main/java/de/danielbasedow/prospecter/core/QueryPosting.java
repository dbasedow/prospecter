package de.danielbasedow.prospecter.core;

/**
 * Immutable representation of query posting (queryId, bitToToggle)
 */
public class QueryPosting {
    private long queryId;
    private short queryBit;

    public QueryPosting(long queryId, short queryBit) {
        this.queryId = queryId;
        this.queryBit = queryBit;
    }

    public long getQueryId() {
        return queryId;
    }

    public short getQueryBit() {
        return queryBit;
    }
}
