package de.danielbasedow.prospecter.core;

/**
 * A Condition is represented by a field name and token combination.
 */
public class Condition {
    private final String fieldName;
    private final Token token;

    public Condition(String fieldName, Token token) {
        this.fieldName = fieldName;
        this.token = token;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Token getToken() {
        return token;
    }
}