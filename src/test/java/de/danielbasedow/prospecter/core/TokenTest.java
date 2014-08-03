package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

import java.util.HashMap;

public class TokenTest extends TestCase {
    public void testEquals() {
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.EQUALS);
        Token<Integer> t2 = new Token<Integer>(1, MatchCondition.EQUALS);
        assertEquals(true, t1.equals(t2));
    }

    public void testHashmapKey() {
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.EQUALS);
        Token<Integer> t2 = new Token<Integer>(1, MatchCondition.EQUALS);
        HashMap<Token, String> hashMap = new HashMap<Token, String>();
        hashMap.put(t1, "foo");
        assertEquals("foo", hashMap.get(t2));
    }
}
