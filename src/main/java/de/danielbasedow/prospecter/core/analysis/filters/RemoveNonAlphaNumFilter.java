package de.danielbasedow.prospecter.core.analysis.filters;

import de.danielbasedow.prospecter.core.analysis.Filter;

public class RemoveNonAlphaNumFilter implements Filter {
    @Override
    public String filter(String input) {
        return input.replaceAll("[^\\sa-zA-Z0-9\\-_]", "");
    }
}
