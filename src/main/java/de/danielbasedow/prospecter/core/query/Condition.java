package de.danielbasedow.prospecter.core.query;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.query.build.ClauseNode;

/**
 * A Condition is represented by a field name and token combination.
 */
public class Condition implements ClauseNode {
    private final String fieldName;
    private final Token token;

    /**
     * if not is set the query bitmask will be set to zero at this position
     */
    private final boolean not;

    public Condition(String fieldName, Token token) {
        this.fieldName = fieldName;
        this.token = token;
        this.not = false;
    }

    public Condition(String fieldName, Token token, boolean not) {
        this.fieldName = fieldName;
        this.token = token;
        this.not = not;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Token getToken() {
        return token;
    }

    public boolean isNot() {
        return not;
    }

    @Override
    public boolean isLeaf() {
        return true;
    }

    /**
     * AIMA symbols need a unique, java conform name
     *
     * @return symbol name for use in AIMA
     */
    public String getSymbolName() {
        return fieldName + token.getCondition().name() + token.getToken().toString();
    }
}