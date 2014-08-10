package de.danielbasedow.prospecter.core.document;


import de.danielbasedow.prospecter.core.Token;

import java.util.List;

/**
 * Represents a field of a document.
 */
public class Field {
    protected String name;
    protected List<Token> tokens;

    /**
     * @param name   name of the field
     * @param tokens List of tokens in the field
     */
    public Field(String name, List<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public String getName() {
        return name;
    }

}
