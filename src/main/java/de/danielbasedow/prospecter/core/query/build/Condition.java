package de.danielbasedow.prospecter.core.query.build;


public class Condition<T> {
    private final String fieldName;
    private final String matchCondition;
    private final Value<T> value;

    public Condition(String fieldName, String matchCondition, Value<T> value) {
        this.fieldName = fieldName;
        this.matchCondition = matchCondition;
        this.value = value;
    }
}
