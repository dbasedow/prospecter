package de.danielbasedow.prospecter.core;

import com.google.inject.Inject;

import java.util.ArrayList;

public class QueryBuilder {
    Tokenizer tokenizer;

    @Inject
    public QueryBuilder(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public String normalize(String str) {
        str = str.toLowerCase();
        return str;
    }

    public Query buildFromString(Long queryId, String query) {
        String normalized = normalize(query);
        String[] tokens = normalized.split("\\s+");
        return new Query(queryId, tokenizer.getTermIds(tokens));
    }
}
