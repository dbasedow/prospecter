package de.danielbasedow.prospecter.core.query;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import junit.framework.TestCase;

public class ConditionTest extends TestCase {
    public void testSymbolName() {
        Token<String> t = new Token<String>("bar");
        Condition condition = new Condition("foo", t);
        assertEquals("fooNONEbar", condition.getSymbolName());

        t = new Token<String>("bar", MatchCondition.EQUALS);
        condition = new Condition("foo", t);
        assertEquals("fooEQUALSbar", condition.getSymbolName());
    }
}
