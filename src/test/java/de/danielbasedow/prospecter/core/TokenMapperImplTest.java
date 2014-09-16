package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

public class TokenMapperImplTest extends TestCase {
    public void testTokenization() {
        TokenMapperImpl tokenizer = new TokenMapperImpl();
        assertEquals(1, tokenizer.getTermId("foo", false));
        assertEquals(2, tokenizer.getTermId("bar", false));
        assertEquals(2, tokenizer.getTermId("bar", false));
        assertEquals(1, tokenizer.getTermId("foo", false));
    }
}
