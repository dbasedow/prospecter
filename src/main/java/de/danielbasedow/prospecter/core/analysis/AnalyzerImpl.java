package de.danielbasedow.prospecter.core.analysis;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.TokenMapper;

import java.util.ArrayList;

public class AnalyzerImpl implements Analyzer {
    protected ArrayList<Filter> filterChain;
    private TokenMapper tokenMapper;
    private Tokenizer tokenizer;

    @Inject
    public AnalyzerImpl(TokenMapper tokenMapper, Tokenizer tokenizer) {
        this.tokenMapper = tokenMapper;
        this.tokenizer = tokenizer;
        filterChain = new ArrayList<Filter>();
    }

    public void addFilter(Filter filter) {
        filterChain.add(filter);
    }

    protected String applyFilters(String input) {
        for (Filter filter : filterChain) {
            input = filter.filter(input);
        }
        return input;
    }

    public ArrayList<Integer> tokenize(String input) {
        ArrayList<Integer> tokenIds = new ArrayList<Integer>();
        input = applyFilters(input);
        ArrayList<String> tokens = tokenizer.tokenize(input);
        return tokenMapper.getTermIds(tokens);
    }

}
