package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import junit.framework.TestCase;

public class FullTextIndexTest extends TestCase {
    public void test() {
        FullTextIndex ft = new FullTextIndex("_all", null);
        assertEquals(0, ft.index.size());
        Token token = new Token<Integer>(1, MatchCondition.EQUALS);
        ft.addPosting(token, QueryPosting.pack(1, 1, false));
        assertEquals(1, ft.index.size());

        ft.removePosting(token, QueryPosting.pack(1, 2, false));
        assertEquals(1, ft.index.get((Integer) token.getToken()).size());

        ft.removePosting(token, QueryPosting.pack(1, 1, false));
        assertEquals(0, ft.index.get((Integer) token.getToken()).size());
    }
}
