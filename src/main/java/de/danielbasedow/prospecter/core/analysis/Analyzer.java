package de.danielbasedow.prospecter.core.analysis;

import de.danielbasedow.prospecter.core.Token;

import java.util.ArrayList;

public interface Analyzer {
    public ArrayList<Token> tokenize(String input);

    public ArrayList<Token> tokenize(String input, boolean dontGenerateNewIds);
}
