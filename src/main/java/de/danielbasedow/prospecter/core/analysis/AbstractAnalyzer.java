package de.danielbasedow.prospecter.core.analysis;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class AbstractAnalyzer implements Analyzer {
    public static Analyzer make(JsonNode options) {
        throw new UnsupportedOperationException();
    }
}
