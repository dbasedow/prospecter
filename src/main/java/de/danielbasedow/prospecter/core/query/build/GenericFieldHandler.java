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

        boolean not = false;
        if (root.get("not") != null) {
            not = root.get("not").asBoolean(false);
        }

        JsonNode valNode = getValue();

        if (valNode.getNodeType() == JsonNodeType.ARRAY) {
            List<Token> tokens = new ArrayList<Token>();
            for (JsonNode node : valNode) {
                tokens.add(getToken(node, matchCondition));
            }
            conditions.add(new Condition(fieldName, new Token<List<Token>>(tokens, MatchCondition.IN), not));
        } else {
            conditions.add(new Condition(fieldName, getToken(valNode, matchCondition), not));
        }
        return conditions;
    }

    protected abstract Token getToken(JsonNode node, MatchCondition matchCondition);
}
