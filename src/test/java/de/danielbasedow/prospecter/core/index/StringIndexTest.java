package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.array.TLongArrayList;
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

        index.addPosting(new Token("bar"), new QueryPosting(1, (short) 1));

        TLongArrayList postings = index.match(field);
        assertEquals(1, postings.size());
    }

}
