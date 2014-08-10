package de.danielbasedow.prospecter.core.analysis.filters;

import de.danielbasedow.prospecter.core.analysis.Filter;

/**
 * Filter to lower-case all characters in a string
 */
public class ToLowerCaseFilter implements Filter {
    @Override
    public String filter(String input) {
        return input.toLowerCase();
    }
}
