package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

public class QueryPostingTest extends TestCase {

    public void testPacking() {
        long l;
        int[] ab;
        l = QueryPosting.pack(0, 0);
        ab = QueryPosting.unpack(l);
        assertEquals(0, ab[0]);
        assertEquals(0, ab[1]);

        l = QueryPosting.pack(1000, 2000);
        ab = QueryPosting.unpack(l);
        assertEquals(1000, ab[0]);
        assertEquals(2000, ab[1]);

        l = QueryPosting.pack(Integer.MAX_VALUE, Integer.MAX_VALUE);
        ab = QueryPosting.unpack(l);
        assertEquals(Integer.MAX_VALUE, ab[0]);
        assertEquals(Integer.MAX_VALUE, ab[1]);

        l = QueryPosting.pack(Integer.MIN_VALUE, Integer.MIN_VALUE);
        ab = QueryPosting.unpack(l);
        assertEquals(Integer.MIN_VALUE, ab[0]);
        assertEquals(Integer.MIN_VALUE, ab[1]);
    }
}