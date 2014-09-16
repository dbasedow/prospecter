package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.query.QueryPosting;
import junit.framework.TestCase;

public class QueryPostingTest extends TestCase {

    public void testPacking() {
        long l;
        int[] ab;
        l = QueryPosting.pack(0, 0, false);
        ab = QueryPosting.unpack(l);
        assertEquals(0, ab[0]);
        assertEquals(0, ab[1]);

        l = QueryPosting.pack(1000, 2000, true);
        ab = QueryPosting.unpack(l);
        assertEquals(1000, ab[0]);
        assertEquals(2000, ab[1]);
        assertEquals(1, ab[2]);

        l = QueryPosting.pack(1000, 2000, false);
        ab = QueryPosting.unpack(l);
        assertEquals(1000, ab[0]);
        assertEquals(2000, ab[1]);
        assertEquals(0, ab[2]);


        l = QueryPosting.pack(Integer.MAX_VALUE, Integer.MAX_VALUE / 2, true);
        ab = QueryPosting.unpack(l);
        assertEquals(Integer.MAX_VALUE, ab[0]);
        assertEquals(Integer.MAX_VALUE / 2, ab[1]);
        assertEquals(1, ab[2]);

        l = QueryPosting.pack(Integer.MAX_VALUE, Integer.MAX_VALUE / 2, false);
        ab = QueryPosting.unpack(l);
        assertEquals(Integer.MAX_VALUE, ab[0]);
        assertEquals(Integer.MAX_VALUE / 2, ab[1]);
        assertEquals(0, ab[2]);

        l = QueryPosting.pack(Integer.MIN_VALUE, Integer.MIN_VALUE / 2, true);
        ab = QueryPosting.unpack(l);
        assertEquals(Integer.MIN_VALUE, ab[0]);
        assertEquals(Integer.MIN_VALUE / 2, ab[1]);
        assertEquals(1, ab[2]);

        l = QueryPosting.pack(Integer.MIN_VALUE, Integer.MIN_VALUE / 2, false);
        ab = QueryPosting.unpack(l);
        assertEquals(Integer.MIN_VALUE, ab[0]);
        assertEquals(Integer.MIN_VALUE / 2, ab[1]);
        assertEquals(0, ab[2]);
    }
}