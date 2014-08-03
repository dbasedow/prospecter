package de.danielbasedow.prospecter.core;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;

public class Query {
    protected Long queryId;
    protected BitSet mask;
    protected List<Token> tokens;

    public Long getQueryId() {
        return queryId;
    }

    public Query(Long queryId, List<Token> tokens) {
        this.tokens = tokens;
        this.queryId = queryId;

        mask = new BitSet(tokens.size());
        mask.set(0, tokens.size(), true); //all bits set to 1
    }

    public HashMap<Token, QueryPosting> getPostings() {
        HashMap<Token, QueryPosting> postings = new HashMap<Token, QueryPosting>();
        short bit = 0;
        for (Token token : tokens) {
            postings.put(token, new QueryPosting(queryId, bit));
            bit++;
        }
        return postings;
    }

    public boolean testBits(BitSet hits) {
        return mask.equals(hits);
    }
}
