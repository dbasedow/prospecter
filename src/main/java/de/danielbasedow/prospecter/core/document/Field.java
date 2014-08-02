package de.danielbasedow.prospecter.core.document;


import de.danielbasedow.prospecter.core.Token;

import java.util.ArrayList;

public interface Field {
    public ArrayList<Token> getTokens();

    public String getName();
}
