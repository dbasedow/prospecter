package de.danielbasedow.prospecter.core.document;

import de.danielbasedow.prospecter.core.Token;

import java.util.ArrayList;

public class TextField implements Field {
    protected String name;
    protected ArrayList<Token> tokens;

    public TextField(String name, ArrayList<Token> tokens) {
        this.name = name;
        this.tokens = tokens;
    }

    @Override
    public ArrayList<Token> getTokens() {
        return tokens;
    }

    @Override
    public String getName() {
        return name;
    }
}
