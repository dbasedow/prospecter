package de.danielbasedow.prospecter.core.document;


import de.danielbasedow.prospecter.core.Token;

import java.util.List;

public class Field {
    protected String name;
    protected List<Token> tokens;

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
