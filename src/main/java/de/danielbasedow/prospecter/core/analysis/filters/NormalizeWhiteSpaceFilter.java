package de.danielbasedow.prospecter.core.analysis.filters;

import de.danielbasedow.prospecter.core.analysis.Filter;

/**
 * Filter to remove redundant whitespace from string
 */
public class NormalizeWhiteSpaceFilter implements Filter {

    @Override
    public String filter(String input) {
        return input.replaceAll("[\\s]{2,}", " ");
    }
}
