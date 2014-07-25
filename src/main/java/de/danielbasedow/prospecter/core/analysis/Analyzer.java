package de.danielbasedow.prospecter.core.analysis;

import java.util.ArrayList;

public interface Analyzer {
    public void addFilter(Filter filter);

    public ArrayList<Integer> tokenize(String input);
}
