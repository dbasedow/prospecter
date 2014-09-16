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

public class IntegerIndexTest extends TestCase {
    private Field makeField(int[] ints) {
        ArrayList<Token> tokens = new ArrayList<Token>();
        for (Integer i : ints) {
            Token<Integer> t = new Token<Integer>(i);
            tokens.add(t);
        }
        return new Field("foo", tokens);
    }

    public void testEquals() {
        IntegerIndex index = new IntegerIndex("foo");
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.EQUALS);
        Token<Integer> t2 = new Token<Integer>(2, MatchCondition.EQUALS);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        index.addPosting(t2, QueryPosting.pack(2, 1, false));
        assertEquals(2, index.index.indexEquals.size());
        assertEquals(0, index.index.indexGreaterThan.size());
        assertEquals(0, index.index.indexLessThan.size());
        int[] int_single = {2};
        Field f = makeField(int_single);
        Matcher matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(1, matcher.getPositiveMatchCount());
        //multiple values in one field:
        int[] ints_multi = {1, 2};
        f = makeField(ints_multi);
        matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(2, matcher.getPositiveMatchCount());
    }

    public void testGreater() {
        IntegerIndex index = new IntegerIndex("foo");
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.GREATER_THAN);
        Token<Integer> t2 = new Token<Integer>(10, MatchCondition.GREATER_THAN);
        Token<Integer> t3 = new Token<Integer>(100, MatchCondition.GREATER_THAN);
        Token<Integer> t4 = new Token<Integer>(-100, MatchCondition.GREATER_THAN);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        index.addPosting(t2, QueryPosting.pack(2, 1, false));
        index.addPosting(t3, QueryPosting.pack(3, 1, false));
        index.addPosting(t4, QueryPosting.pack(4, 1, false));
        assertEquals(0, index.index.indexEquals.size());
        assertEquals(4, index.index.indexGreaterThan.size());
        assertEquals(0, index.index.indexLessThan.size());
        int[] int_single = {2};
        Field f = makeField(int_single);
        Matcher matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(2, matcher.getPositiveMatchCount());

        int[] int_high = {101};
        f = makeField(int_high);
        matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(4, matcher.getPositiveMatchCount());

        //deletion
        index.removePosting(t3, QueryPosting.pack(3, 1, false));
        f = makeField(int_high);
        matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(3, matcher.getPositiveMatchCount());
    }

    public void testGreaterEqual() {
        IntegerIndex index = new IntegerIndex("foo");
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.GREATER_THAN_EQUALS);
        Token<Integer> t2 = new Token<Integer>(10, MatchCondition.GREATER_THAN_EQUALS);
        Token<Integer> t3 = new Token<Integer>(100, MatchCondition.GREATER_THAN_EQUALS);
        Token<Integer> t4 = new Token<Integer>(-100, MatchCondition.GREATER_THAN_EQUALS);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        index.addPosting(t2, QueryPosting.pack(2, 1, false));
        index.addPosting(t3, QueryPosting.pack(3, 1, false));
        index.addPosting(t4, QueryPosting.pack(4, 1, false));

        assertEquals(4, index.index.indexEquals.size());
        assertEquals(4, index.index.indexGreaterThan.size());
        assertEquals(0, index.index.indexLessThan.size());

        int[] int_single = {2};
        Field f = makeField(int_single);
        Matcher matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(2, matcher.getPositiveMatchCount());

        int[] int_high = {100};
        f = makeField(int_high);
        matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(4, matcher.getPositiveMatchCount());
    }

    public void testLess() {
        IntegerIndex index = new IntegerIndex("foo");
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.LESS_THAN);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        assertEquals(0, index.index.indexEquals.size());
        assertEquals(0, index.index.indexGreaterThan.size());
        assertEquals(1, index.index.indexLessThan.size());

        int[] int_single = {0};
        Field f = makeField(int_single);
        Matcher matcher = new Matcher(new QueryManager());
        index.match(f, matcher);
        assertEquals(1, matcher.getPositiveMatchCount());
    }

    public void testLessEqual() {
        IntegerIndex index = new IntegerIndex("foo");
        Token<Integer> t1 = new Token<Integer>(1, MatchCondition.LESS_THAN_EQUALS);
        index.addPosting(t1, QueryPosting.pack(1, 1, false));
        assertEquals(1, index.index.indexEquals.size());
        assertEquals(0, index.index.indexGreaterThan.size());
        assertEquals(1, index.index.indexLessThan.size());
    }

}
