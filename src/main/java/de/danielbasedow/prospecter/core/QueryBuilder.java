package de.danielbasedow.prospecter.core;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.analysis.Analyzer;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder {
    protected Analyzer analyzer;

    @Inject
    public QueryBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Query buildFromString(Long queryId, String query) {
        List<Token> tokenIds = analyzer.tokenize(query);
        return new Query(queryId, tokenIds);
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
