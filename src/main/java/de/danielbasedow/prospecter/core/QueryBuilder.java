package de.danielbasedow.prospecter.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.analysis.TokenizerException;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.schema.Schema;

import java.util.ArrayList;
import java.util.List;

/**
 * Makes Query instances from JSON query definitions
 */
public class QueryBuilder {
    private Schema schema;

    public QueryBuilder(Schema schema) {
        this.schema = schema;
    }

    /**
     * Build a Query instance from a JSON String
     *
     * @param json JSON string
     * @return query instance
     * @throws MalformedQueryException
     */
    public Query buildFromJSON(String json) throws MalformedQueryException {
        List<Condition> conditions = new ArrayList<Condition>();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(json);
            Long queryId = root.get("id").asLong();
            for (JsonNode node : root.get("query").get("conditions")) {
                conditions.addAll(handleCondition((ObjectNode) node));
            }
            return new Query(queryId, conditions);
        } catch (Exception e) {
            throw new MalformedQueryException();
        }
    }

    /**
     * Handle Condition encountered in JSON structure. One condition in JSON can result in several Condition objects.
     *
     * @param node ObjectNode for the condition
     * @return list of conditions
     * @throws MalformedQueryException
     */
    protected List<Condition> handleCondition(ObjectNode node) throws MalformedQueryException {
        String fieldName = node.get("field").asText();
        FieldIndex fieldIndex = schema.getFieldIndex(fieldName);
        switch (fieldIndex.getFieldType()) {
            case FULL_TEXT:
                try {
                    return handleFullText(fieldName, node);
                } catch (TokenizerException e) {
                    e.printStackTrace();
                }
            case INTEGER:
                return handleInteger(fieldName, node);
            case GEO_DISTANCE:
                return handleGeoDistance(fieldName, node);
        }
        throw new MalformedQueryException();
    }

    private List<Condition> handleInteger(String fieldName, ObjectNode node) {
        Integer value = node.get("value").asInt();
        String comparator = node.get("condition").asText();
        MatchCondition matchCondition = MatchCondition.NONE;
        if ("gt".equals(comparator)) {
            matchCondition = MatchCondition.GREATER_THAN;
        } else if ("gte".equals(comparator)) {
            matchCondition = MatchCondition.GREATER_THAN_EQUALS;
        } else if ("lt".equals(comparator)) {
            matchCondition = MatchCondition.LESS_THAN;
        } else if ("lte".equals(comparator)) {
            matchCondition = MatchCondition.LESS_THAN_EQUALS;
        } else if ("eq".equals(comparator)) {
            matchCondition = MatchCondition.EQUALS;
        }
        Token<Integer> token = new Token<Integer>(value, matchCondition);
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(fieldName, token));
        return conditions;
    }

    public List<Condition> handleFullText(String fieldName, ObjectNode node) throws TokenizerException {
        String query = node.get("value").asText();
        Analyzer analyzer = ((FullTextIndex) schema.getFieldIndex(fieldName)).getAnalyzer();

        List<Condition> conditions = new ArrayList<Condition>();
        for (Token token : analyzer.tokenize(query)) {
            conditions.add(new Condition(fieldName, token));
        }
        return conditions;
    }

    public List<Condition> handleGeoDistance(String fieldName, ObjectNode node) {
        ObjectNode location = (ObjectNode) node.get("value");
        List<Condition> conditions = new ArrayList<Condition>();

        GeoPerimeter geo = new GeoPerimeter(
                location.get("lat").asDouble(),
                location.get("lng").asDouble(),
                location.get("distance").asInt()
        );
        conditions.add(new Condition(fieldName, new Token<GeoPerimeter>(geo)));

        return conditions;
    }
}
