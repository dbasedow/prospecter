package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

public class TokenMapperImplTest extends TestCase {
    public void testTokenization() {
        TokenMapperImpl tokenizer = new TokenMapperImpl();
        assertEquals(0, tokenizer.getTermId("foo").intValue());
        assertEquals(1, tokenizer.getTermId("bar").intValue());
        assertEquals(1, tokenizer.getTermId("bar").intValue());
        assertEquals(0, tokenizer.getTermId("foo").intValue());
    }
}
