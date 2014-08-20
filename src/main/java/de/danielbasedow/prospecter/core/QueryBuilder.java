package de.danielbasedow.prospecter.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.analysis.TokenizerException;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.index.DateTimeIndex;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Makes Query instances from JSON query definitions
 */
public class QueryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilder.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(json);
            Long queryId = root.get("id").asLong();
            for (JsonNode node : root.get("query").get("conditions")) {
                conditions.addAll(handleCondition((ObjectNode) node));
            }
            return new Query(queryId, conditions);
        } catch (Exception e) {
            throw new MalformedQueryException("Error parsing query", e);
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
        if (fieldIndex == null) {
            LOGGER.error("field '" + fieldName + "' not in schema");
            throw new MalformedQueryException("Field '" + fieldName + "' not in schema.");
        }
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
            case DATE_TIME:
                return handleDateTime(fieldName, node);
            case LONG:
                return handleLong(fieldName, node);
            case DOUBLE:
                return handleDouble(fieldName, node);
            case STRING:
                return handleString(fieldName, node);
        }
        throw new MalformedQueryException("Field '" + fieldName + "' does not seem to be supported.");
    }

    private List<Condition> handleString(String fieldName, ObjectNode node) {
        String value = node.get("value").asText();
        Token<String> token = new Token<String>(value, MatchCondition.EQUALS);
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(fieldName, token));
        return conditions;
    }

    private List<Condition> handleLong(String fieldName, ObjectNode node) {
        Long value = node.get("value").asLong();
        String comparator = node.get("condition").asText();
        MatchCondition matchCondition = getMatchCondition(comparator);
        Token<Long> token = new Token<Long>(value, matchCondition);
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(fieldName, token));
        return conditions;
    }

    private List<Condition> handleDouble(String fieldName, ObjectNode node) {
        Double value = node.get("value").asDouble();
        String comparator = node.get("condition").asText();
        MatchCondition matchCondition = getMatchCondition(comparator);
        Token<Double> token = new Token<Double>(value, matchCondition);
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(fieldName, token));
        return conditions;
    }

    private MatchCondition getMatchCondition(String comparator) {
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

    private List<Condition> handleInteger(String fieldName, ObjectNode node) {
        Integer value = node.get("value").asInt();
        String comparator = node.get("condition").asText();
        MatchCondition matchCondition = getMatchCondition(comparator);
        Token<Integer> token = new Token<Integer>(value, matchCondition);
        List<Condition> conditions = new ArrayList<Condition>();
        conditions.add(new Condition(fieldName, token));
        return conditions;
    }

    private List<Condition> handleDateTime(String fieldName, ObjectNode node) throws MalformedQueryException {
        String rawValue = node.get("value").asText();
        try {
            //Get DateFormat from Schema and parse value according to format
            Date date = ((DateTimeIndex) schema.getFieldIndex(fieldName)).getDateFormat().parse(rawValue);
            String comparator = node.get("condition").asText();
            MatchCondition matchCondition = getMatchCondition(comparator);
            Token<Long> token = new Token<Long>(date.getTime(), matchCondition);
            List<Condition> conditions = new ArrayList<Condition>();
            conditions.add(new Condition(fieldName, token));
            return conditions;
        } catch (ParseException e) {
            throw new MalformedQueryException("Date could not be parsed: '" + rawValue + "'", e);
        }
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
