package de.danielbasedow.prospecter.benchmark;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.index.IntegerIndex;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import gnu.trove.list.TLongList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IntegerIndexTest {
    public static void main(String[] args) {
        IntegerIndex index = new IntegerIndex("foo");
        System.out.print((new Date()).getTime());
        System.out.println(" start filling index");
        fillIndex(index, 1000000, MatchCondition.GREATER_THAN, 10000);
        System.out.print((new Date()).getTime());
        System.out.println(" done filling index");

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<Integer>(5000, MatchCondition.NONE));

        System.out.print((new Date()).getTime());
        System.out.println(" start matching");
        TLongList postings = index.match(new Field("foo", tokens));
        System.out.print((new Date()).getTime());
        System.out.println(" done matching");
        System.out.println("Matched: " + Integer.toString(postings.size()));
    }

    private static void fillIndex(IntegerIndex index, int count, MatchCondition condition, int max) {
        for (int i = 0; i < count; i++) {
            int randInt = (int) (Math.random() * max);
            Token<Integer> token = new Token<Integer>(randInt, condition);
            index.addPosting(token, QueryPosting.pack(1, 1));
        }
    }

}
