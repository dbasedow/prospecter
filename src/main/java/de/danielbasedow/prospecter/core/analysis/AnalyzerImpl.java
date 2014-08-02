package de.danielbasedow.prospecter.core.analysis;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.TokenMapper;

import java.util.ArrayList;
import java.util.Set;

public class AnalyzerImpl implements Analyzer {
    protected Set<Filter> filterChain;
    private TokenMapper tokenMapper;
    private Tokenizer tokenizer;

    @Inject
    public AnalyzerImpl(TokenMapper tokenMapper, Tokenizer tokenizer, Set<Filter> filterChain) {
        this.tokenMapper = tokenMapper;
        this.tokenizer = tokenizer;
        this.filterChain = filterChain;
    }

    protected String applyFilters(String input) {
        for (Filter filter : filterChain) {
            input = filter.filter(input);
        }
        return input;
    }

    public ArrayList<Integer> tokenize(String input) {
        return tokenize(input, false);
    }

    public ArrayList<Integer> tokenize(String input, boolean dontGenerateNewIds) {
        input = applyFilters(input);
        ArrayList<String> tokens = tokenizer.tokenize(input);
        return tokenMapper.getTermIds(tokens, dontGenerateNewIds);
    }

}
