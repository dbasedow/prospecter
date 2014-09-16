package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import gnu.trove.list.TLongList;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class StringIndexTest extends TestCase {
    public void test() {
        StringIndex index = new StringIndex("foo");
        Token<String> token = new Token<String>("bar");
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(token);
        Field field = new Field("foo", tokens);

        index.addPosting(new Token("bar"), QueryPosting.pack(1, 1));

        Matcher matcher = new Matcher(new QueryManager());
        TLongList postings = index.match(field, matcher);
        assertEquals(1, postings.size());

        index.removePosting(new Token("bar"), QueryPosting.pack(1, 1));
        matcher = new Matcher(new QueryManager());
        postings = index.match(field, matcher);
        assertEquals(0, postings.size());
    }

}
