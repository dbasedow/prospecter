package de.danielbasedow.prospecter.core.analysis;

import de.danielbasedow.prospecter.core.Token;

import java.util.List;

public interface Analyzer {
    public List<Token> tokenize(String input) throws TokenizerException;

    public List<Token> tokenize(String input, boolean dontGenerateNewIds) throws TokenizerException;
}
