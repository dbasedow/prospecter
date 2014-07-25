package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

public class TokenMapperImplTest extends TestCase {
    public void testTokenization() {
        TokenMapperImpl tokenizer = new TokenMapperImpl();
        assertEquals(0, tokenizer.getTermId("foo", false).intValue());
        assertEquals(1, tokenizer.getTermId("bar", false).intValue());
        assertEquals(1, tokenizer.getTermId("bar", false).intValue());
        assertEquals(0, tokenizer.getTermId("foo", false).intValue());
    }
}
