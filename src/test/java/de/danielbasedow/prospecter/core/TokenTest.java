package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

import java.util.HashMap;

public class TokenTest extends TestCase {
    public void testEquals() {
        Token<Integer> t1 = new Token<Integer>(1);
        Token<Integer> t2 = new Token<Integer>(1);
        assertEquals(true, t1.equals(t2));
    }

    public void testHashmapKey() {
        Token<Integer> t1 = new Token<Integer>(1);
        Token<Integer> t2 = new Token<Integer>(1);
        HashMap<Token, String> hashMap = new HashMap<Token, String>();
        hashMap.put(t1, "foo");
        assertEquals("foo", hashMap.get(t2));
    }
}
