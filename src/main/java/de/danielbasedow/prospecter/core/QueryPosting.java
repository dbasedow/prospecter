package de.danielbasedow.prospecter.core;

/**
 * Immutable representation of query posting (queryId, bitToToggle)
 */
public class QueryPosting {
    public static final int QUERY_ID_INDEX = 0;
    public static final int QUERY_BIT_INDEX = 1;

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

    public long getPackedPosting() {
        return pack(queryId, queryBit);
    }

    public static long pack(int queryId, int queryBit) {
        return (((long) queryId) << 32) | (queryBit & 0xffffffffL);
    }

    public static int[] unpack(long posting) {
        return new int[]{
                (int) (posting >> 32),
                (int) ((posting) & 0xffffffffl)
        };
    }
}
