package de.danielbasedow.prospecter.core.query.build;


import junit.framework.TestCase;

public class PropositionSymbolTest extends TestCase {
    public void test() {
        PropositionSymbol symbol = new PropositionSymbol("foo", null);
        assertEquals("foo", symbol.getSymbol());
        symbol = new PropositionSymbol("foo.", null);
        assertEquals("foo", symbol.getSymbol());
    }
}
