package de.danielbasedow.prospecter.core;

import java.util.ArrayList;
import java.util.List;


public interface TokenMapper {
    Integer getTermId(String str, boolean dontGenerateNewIds);

    Integer getNewTermId();

    public List<Token> getTermIds(List<String> tokens);

    public List<Token> getTermIds(List<String> tokens, boolean dontGenerateNewIds);
}
