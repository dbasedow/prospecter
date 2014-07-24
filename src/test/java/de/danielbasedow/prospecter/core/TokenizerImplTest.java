package de.danielbasedow.prospecter.core;

import junit.framework.TestCase;

public class TokenizerImplTest extends TestCase {
    public void testTokenization() {
        TokenizerImpl tokenizer = new TokenizerImpl();
        assertEquals(0, tokenizer.getTermId("foo").intValue());
        assertEquals(1, tokenizer.getTermId("bar").intValue());
        assertEquals(1, tokenizer.getTermId("bar").intValue());
        assertEquals(0, tokenizer.getTermId("foo").intValue());
    }
}
