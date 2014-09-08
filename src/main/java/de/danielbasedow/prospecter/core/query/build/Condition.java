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

    public String getFieldName() {
        return fieldName;
    }

    public String getMatchCondition() {
        return matchCondition;
    }

    public Value<T> getValue() {
        return value;
    }
}
