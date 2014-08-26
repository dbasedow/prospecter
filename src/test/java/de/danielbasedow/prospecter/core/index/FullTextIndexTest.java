package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import junit.framework.TestCase;

public class FullTextIndexTest extends TestCase {
    public void test() {
        FullTextIndex ft = new FullTextIndex("_all", null);
        assertEquals(0, ft.index.size());
        ft.addPosting(new Token<Integer>(1, MatchCondition.EQUALS), QueryPosting.pack(1, 1));
        assertEquals(1, ft.index.size());
    }
}
