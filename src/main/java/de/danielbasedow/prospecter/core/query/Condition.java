package de.danielbasedow.prospecter.core.query;

import de.danielbasedow.prospecter.core.Token;

/**
 * A Condition is represented by a field name and token combination.
 */
public class Condition {
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
}