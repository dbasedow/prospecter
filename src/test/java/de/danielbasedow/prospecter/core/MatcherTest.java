package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import junit.framework.TestCase;

import java.util.ArrayList;

public class MatcherTest extends TestCase {

    public void test() {
        FullTextIndex ft = new FullTextIndex("_all");
        QueryPosting posting = new QueryPosting(1, (short) 1);
        ft.addPosting(new Token<Integer>(1, MatchCondition.EQUALS), posting);
        Matcher m = new Matcher(new QueryManager());
        ArrayList<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<Integer>(1, MatchCondition.EQUALS));
        m.addHits(ft.match(new Field("_all", tokens)));
        assertEquals(1, m.hits.size());
    }
}
