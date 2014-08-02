package de.danielbasedow.prospecter.core;

import java.util.ArrayList;


public interface TokenMapper {
    Integer getTermId(String str, boolean dontGenerateNewIds);

    Integer getNewTermId();

    public ArrayList<Token> getTermIds(ArrayList<String> tokens);

    public ArrayList<Token> getTermIds(ArrayList<String> tokens, boolean dontGenerateNewIds);
}
