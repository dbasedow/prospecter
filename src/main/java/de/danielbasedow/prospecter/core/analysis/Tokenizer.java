package de.danielbasedow.prospecter.core.analysis;

import java.util.List;

/**
 * Interface for tokenizer used in AnalyzerImpl
 */
public interface Tokenizer {
    /**
     * Produce a List of String tokens from a string
     *
     * @param input filtered input string
     * @return list of strings, usable as input for TokenMapper
     */
    public List<String> tokenize(String input);
}
