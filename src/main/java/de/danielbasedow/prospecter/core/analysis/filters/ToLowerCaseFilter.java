package de.danielbasedow.prospecter.core.analysis.filters;

import de.danielbasedow.prospecter.core.analysis.Filter;

public class ToLowerCaseFilter implements Filter {
    @Override
    public String filter(String input) {
        return input.toLowerCase();
    }
}
