package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import junit.framework.TestCase;

public class FullTextIndexTest extends TestCase {
    public void test() {
        FullTextIndex ft = new FullTextIndex("_all");
        assertEquals(0, ft.index.size());
        QueryPosting posting = new QueryPosting(1, (short) 1);
        ft.addPosting(new Token<Integer>(1, MatchCondition.EQUALS), posting);
        assertEquals(1, ft.index.size());
    }
}
