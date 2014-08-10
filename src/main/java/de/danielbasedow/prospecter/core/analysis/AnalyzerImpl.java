package de.danielbasedow.prospecter.core.analysis;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.TokenMapper;

import java.util.List;
import java.util.Set;

/**
 * Simple implementation of Analyzer interface. Works even if lucene is not available.
 */
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

    public List<Token> tokenize(String input) {
        return tokenize(input, false);
    }

    public List<Token> tokenize(String input, boolean dontGenerateNewIds) {
        input = applyFilters(input);
        List<String> tokens = tokenizer.tokenize(input);
        return tokenMapper.getTermIds(tokens, dontGenerateNewIds);
    }

}
