package de.danielbasedow.prospecter.core.analysis;

import java.util.ArrayList;

public interface Analyzer {
    public ArrayList<Integer> tokenize(String input);

    public ArrayList<Integer> tokenize(String input, boolean dontGenerateNewIds);
}
