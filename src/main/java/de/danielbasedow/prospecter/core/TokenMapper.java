package de.danielbasedow.prospecter.core;

public interface TokenMapper {
    int getTermId(String str, boolean dontGenerateNewIds);
}
