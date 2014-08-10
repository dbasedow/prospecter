package de.danielbasedow.prospecter.core.analysis;

/**
 * Interface for Filter to be used in AnalyzerImpl filter chain.
 */
public interface Filter {
    /**
     * Do the actual filtering
     *
     * @param input input String
     * @return filtered String
     */
    public String filter(String input);
}
