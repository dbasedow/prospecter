package de.danielbasedow.prospecter.core.query.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.query.InvalidQueryException;

public class AbstractFieldHandler implements FieldHandler {
    protected ObjectNode root;
    protected String fieldName;

    public AbstractFieldHandler(ObjectNode node, String fieldName) {
        root = node;
        this.fieldName = fieldName;
    }

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

    protected JsonNode getValue() {
        JsonNode valNode = root.get("value");
        if (valNode == null) {
            throw new InvalidQueryException("No value node found");
        }
        return valNode;
    }
}
