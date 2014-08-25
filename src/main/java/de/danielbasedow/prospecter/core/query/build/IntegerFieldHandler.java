package de.danielbasedow.prospecter.core.query.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.query.Condition;
import de.danielbasedow.prospecter.core.query.InvalidQueryException;

import java.util.ArrayList;
import java.util.List;

public class IntegerFieldHandler extends AbstractFieldHandler {
    private ObjectNode root;

    public IntegerFieldHandler(ObjectNode node) {
        root = node;
    }

    public List<Condition> getConditions() {
        List<Condition> conditions = new ArrayList<Condition>();

        String fieldName = root.get("condition").asText();

        String comparator = root.get("condition").asText();
        MatchCondition matchCondition = getMatchCondition(comparator);

        JsonNode valNode = root.get("value");
        if (valNode == null) {
            throw new InvalidQueryException("No value node found");
        }

        if (valNode.getNodeType() == JsonNodeType.ARRAY) {
            for (JsonNode node : valNode) {
                conditions.add(makeCondition(matchCondition, node.asInt(), fieldName));
            }
        } else {
            conditions.add(makeCondition(matchCondition, valNode.asInt(), fieldName));
        }
        return conditions;
    }

    private Condition makeCondition(MatchCondition matchCondition, Integer token, String fieldName) {
        return new Condition(fieldName, new Token<Integer>(token, matchCondition));
    }
}
