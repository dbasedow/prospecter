package de.danielbasedow.prospecter.core.query.build;

import de.danielbasedow.prospecter.core.MatchCondition;

public class AbstractFieldHandler implements FieldHandler {

    protected MatchCondition getMatchCondition(String comparator) {
        if ("gt".equals(comparator)) {
            return MatchCondition.GREATER_THAN;
        } else if ("gte".equals(comparator)) {
            return MatchCondition.GREATER_THAN_EQUALS;
        } else if ("lt".equals(comparator)) {
            return MatchCondition.LESS_THAN;
        } else if ("lte".equals(comparator)) {
            return MatchCondition.LESS_THAN_EQUALS;
        } else if ("eq".equals(comparator)) {
            return MatchCondition.EQUALS;
        } else {
            return MatchCondition.NONE;
        }
    }
}
