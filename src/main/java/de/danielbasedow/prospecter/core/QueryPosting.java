package de.danielbasedow.prospecter.core;

/**
 * Immutable representation of query posting (queryId, bitToToggle)
 */
public class QueryPosting {
    private int queryId;
    private short queryBit;

    public QueryPosting(int queryId, short queryBit) {
        this.queryId = queryId;
        this.queryBit = queryBit;
    }

    public int getQueryId() {
        return queryId;
    }

    public short getQueryBit() {
        return queryBit;
    }
}
