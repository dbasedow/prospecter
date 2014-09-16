package de.danielbasedow.prospecter.core.query;

/**
 * Immutable representation of query posting (queryId, bitToToggle)
 */
public class QueryPosting {
    public static final int QUERY_ID_INDEX = 0;
    public static final int QUERY_BIT_INDEX = 1;
    public static final int QUERY_NOT_INDEX = 2;

    private final int queryId;
    private final short queryBit;

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
        return pack(queryId, queryBit, false);
    }

    public static long pack(int queryId, int queryBit) {
        return pack(queryId, queryBit, false);
    }

    public static long pack(int queryId, int queryBit, boolean notFlag) {
        int not = notFlag ? 1 : 0;

        return (((long) queryId) << 32) | ((queryBit << 1 ) & 0xfffffffeL) | ((long) not & 0x00000001L);
    }

    public static int[] unpack(long posting) {
        return new int[]{
                (int) (posting >> 32),
                (int) ((posting) & 0xfffffffel) >> 1,
                (int) ((posting) & 0x00000001l)
        };
    }
}
