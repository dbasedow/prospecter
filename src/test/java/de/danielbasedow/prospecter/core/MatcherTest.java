package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import junit.framework.TestCase;

import java.util.ArrayList;

public class MatcherTest extends TestCase {

    public void test() {
        FullTextIndex ft = new FullTextIndex("_all", null);
        ft.addPosting(new Token<Integer>(1, MatchCondition.EQUALS), QueryPosting.pack(1, 1, false));
        Matcher m = new Matcher(new QueryManager());
        ArrayList<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<Integer>(1, MatchCondition.EQUALS));
        ft.match(new Field("_all", tokens), m);
        assertEquals(1, m.hits.size());
    }
}
