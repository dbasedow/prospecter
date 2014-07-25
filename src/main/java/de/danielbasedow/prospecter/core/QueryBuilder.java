package de.danielbasedow.prospecter.core;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.analysis.Analyzer;

import java.util.ArrayList;

public class QueryBuilder {
    protected Analyzer analyzer;

    @Inject
    public QueryBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Query buildFromString(Long queryId, String query) {
        ArrayList<Integer> tokenIds = analyzer.tokenize(query);
        return new Query(queryId, tokenIds);
    }
}
