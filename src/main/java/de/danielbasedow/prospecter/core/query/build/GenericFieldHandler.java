package de.danielbasedow.prospecter.core.query.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.query.Condition;

import java.util.ArrayList;
import java.util.List;

public abstract class GenericFieldHandler extends AbstractFieldHandler {

    public GenericFieldHandler(ObjectNode node, String fieldName) {
        super(node, fieldName);
    }

    public List<Condition> getConditions() {
        List<Condition> conditions = new ArrayList<Condition>();

        String comparator = root.get("condition").asText();
        MatchCondition matchCondition = getMatchCondition(comparator);

        JsonNode valNode = getValue();

        if (valNode.getNodeType() == JsonNodeType.ARRAY) {
            for (JsonNode node : valNode) {
                conditions.add(new Condition(fieldName, getToken(node, matchCondition)));
            }
        } else {
            conditions.add(new Condition(fieldName, getToken(valNode, matchCondition)));
        }
        return conditions;
    }

    protected abstract Token getToken(JsonNode node, MatchCondition matchCondition);
}
