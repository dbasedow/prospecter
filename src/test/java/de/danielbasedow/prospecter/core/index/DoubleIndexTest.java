package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import gnu.trove.list.TLongList;
import junit.framework.TestCase;

import java.util.ArrayList;

public class DoubleIndexTest extends TestCase {
    private Field makeField(double[] doubles) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (Double i : doubles) {
            Token<Double> t = new Token<Double>(i);
            tokens.add(t);
        }
        return new Field("foo", tokens);
    }

    public void testEquals() {
        DoubleIndex index = new DoubleIndex("foo");
        Token<Double> t1 = new Token<Double>(1.0, MatchCondition.EQUALS);
        Token<Double> t2 = new Token<Double>(2.0, MatchCondition.EQUALS);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        index.addPosting(t2, QueryPosting.pack(2, 1, false));
        assertEquals(2, index.index.indexEquals.size());
        assertEquals(0, index.index.indexGreaterThan.size());
        assertEquals(0, index.index.indexLessThan.size());
        double[] double_single = {2.0};
        Field f = makeField(double_single);
        Matcher matcher = new Matcher(new QueryManager());
        TLongList postings = index.match(f, matcher);
        assertEquals(1, postings.size());
        //multiple values in one field:
        double[] doubles_multiple = {1, 2};
        f = makeField(doubles_multiple);
        matcher = new Matcher(new QueryManager());
        postings = index.match(f, matcher);
        assertEquals(2, postings.size());
    }

    public void testGreater() {
        DoubleIndex index = new DoubleIndex("foo");
        Token<Double> t1 = new Token<Double>(1.0, MatchCondition.GREATER_THAN);
        Token<Double> t2 = new Token<Double>(10.0, MatchCondition.GREATER_THAN);
        Token<Double> t3 = new Token<Double>(100.0, MatchCondition.GREATER_THAN);
        Token<Double> t4 = new Token<Double>(-100.0, MatchCondition.GREATER_THAN);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        index.addPosting(t2, QueryPosting.pack(1, 1, false));
        index.addPosting(t3, QueryPosting.pack(1, 1, false));
        index.addPosting(t4, QueryPosting.pack(1, 1, false));
        assertEquals(0, index.index.indexEquals.size());
        assertEquals(4, index.index.indexGreaterThan.size());
        assertEquals(0, index.index.indexLessThan.size());
        double[] double_single = {2};
        Field f = makeField(double_single);
        Matcher matcher = new Matcher(new QueryManager());
        TLongList postings = index.match(f, matcher);
        assertEquals(2, postings.size());

        double[] double_high = {101};
        f = makeField(double_high);
        matcher = new Matcher(new QueryManager());
        postings = index.match(f, matcher);
        assertEquals(4, postings.size());
    }

    public void testGreaterEqual() {
        DoubleIndex index = new DoubleIndex("foo");
        Token<Double> t1 = new Token<Double>(1.0, MatchCondition.GREATER_THAN_EQUALS);
        Token<Double> t2 = new Token<Double>(10.0, MatchCondition.GREATER_THAN_EQUALS);
        Token<Double> t3 = new Token<Double>(100.0, MatchCondition.GREATER_THAN_EQUALS);
        Token<Double> t4 = new Token<Double>(-100.0, MatchCondition.GREATER_THAN_EQUALS);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        index.addPosting(t2, QueryPosting.pack(1, 1, false));
        index.addPosting(t3, QueryPosting.pack(1, 1, false));
        index.addPosting(t4, QueryPosting.pack(1, 1, false));

        assertEquals(4, index.index.indexEquals.size());
        assertEquals(4, index.index.indexGreaterThan.size());
        assertEquals(0, index.index.indexLessThan.size());

        double[] double_single = {2};
        Field f = makeField(double_single);
        Matcher matcher = new Matcher(new QueryManager());
        TLongList postings = index.match(f, matcher);
        assertEquals(2, postings.size());

        double[] double_high = {100};
        f = makeField(double_high);
        matcher = new Matcher(new QueryManager());
        postings = index.match(f, matcher);
        assertEquals(4, postings.size());
    }

    public void testLess() {
        DoubleIndex index = new DoubleIndex("foo");
        Token<Double> t1 = new Token<Double>(1.0, MatchCondition.LESS_THAN);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        assertEquals(0, index.index.indexEquals.size());
        assertEquals(0, index.index.indexGreaterThan.size());
        assertEquals(1, index.index.indexLessThan.size());

        double[] double_single = {0};
        Field f = makeField(double_single);
        Matcher matcher = new Matcher(new QueryManager());
        TLongList postings = index.match(f, matcher);
        assertEquals(1, postings.size());
    }
}
