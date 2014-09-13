package de.danielbasedow.prospecter.core.query.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.MalformedQueryException;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.analysis.TokenizerException;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.index.DateTimeIndex;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.query.*;
import de.danielbasedow.prospecter.core.query.Condition;
import de.danielbasedow.prospecter.core.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AdvancedQueryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedQueryBuilder.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final Schema schema;

    public AdvancedQueryBuilder(Schema schema) {
        this.schema = schema;
    }

    /**
     * Build a Query instance from a JSON String
     *
     * @param json JSON string
     * @return query instance
     * @throws de.danielbasedow.prospecter.core.MalformedQueryException
     */
    public Query buildFromJSON(String json) throws MalformedQueryException {
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(json);
            int queryId = root.get("id").asInt();

            ClauseNode clauseNode = parseNode(root.get("query"));

            return new Query(queryId, clauseNode);
        } catch (Exception e) {
            throw new MalformedQueryException("Error parsing query", e);
        }
    }

    protected ClauseNode parseNode(JsonNode node) throws MalformedQueryException {
        if (node.getNodeType() == JsonNodeType.OBJECT) {
            if (isCondition(node)) {
                return handleCondition((ObjectNode) node);
            } else {
                return parseClauseNode(node);
            }
        }
        return null;
    }

    protected ClauseNode parseClauseNode(JsonNode node) throws MalformedQueryException {
        Clause.ClauseType clauseType;
        JsonNode arrObj;
        if (node.get("or") != null) {
            clauseType = Clause.ClauseType.OR;
            arrObj = node.get("or");
        } else if (node.get("not") != null) {
            clauseType = Clause.ClauseType.NOT;
            arrObj = node.get("not");
        } else if (node.get("and") != null) {
            clauseType = Clause.ClauseType.AND;
            arrObj = node.get("and");
        } else {
            throw new InvalidQueryException("No known connector found.");
        }
        return new Clause(clauseType, parseClauseChildren((ArrayNode) arrObj));
    }

    protected List<ClauseNode> parseClauseChildren(ArrayNode node) throws MalformedQueryException {
        List<ClauseNode> subClauses = new ArrayList<ClauseNode>();
        for (JsonNode subNode : node) {
            subClauses.add(parseNode(subNode));
        }
        return subClauses;
    }

    protected boolean isCondition(JsonNode node) {
        return node.get("field") != null;
    }

    /**
     * Handle Condition encountered in JSON structure. One condition in JSON can result in several Condition objects,
     * which have to be combined in a Clause using AND
     *
     * @param node ObjectNode for the condition
     * @return Clause containing condition(s) ANDed together
     * @throws MalformedQueryException
     */
    protected ClauseNode handleCondition(ObjectNode node) throws MalformedQueryException {
        String fieldName = node.get("field").asText();
        FieldIndex fieldIndex = schema.getFieldIndex(fieldName);
        if (fieldIndex == null) {
            LOGGER.error("field '" + fieldName + "' not in schema");
            throw new MalformedQueryException("Field '" + fieldName + "' not in schema.");
        }
        GenericFieldHandler handler;
        switch (fieldIndex.getFieldType()) {
            case FULL_TEXT:
                try {
                    return wrap(handleFullText(fieldName, node));
                } catch (TokenizerException e) {
                    e.printStackTrace();
                }
            case INTEGER:
                handler = new GenericFieldHandler(node, fieldName) {
                    @Override
                    protected Token getToken(JsonNode node, MatchCondition matchCondition) {
                        return new Token<Integer>(node.asInt(), matchCondition);
                    }
                };
                return wrap(handler.getConditions());
            case GEO_DISTANCE:
                return wrap(handleGeoDistance(fieldName, node));
            case DATE_TIME:
                return wrap(handleDateTime(fieldName, node));
            case LONG:
                handler = new GenericFieldHandler(node, fieldName) {
                    @Override
                    protected Token getToken(JsonNode node, MatchCondition matchCondition) {
                        return new Token<Long>(node.asLong(), matchCondition);
                    }
                };
                return wrap(handler.getConditions());
            case DOUBLE:
                handler = new GenericFieldHandler(node, fieldName) {
                    @Override
                    protected Token getToken(JsonNode node, MatchCondition matchCondition) {
                        return new Token<Double>(node.asDouble(), matchCondition);
                    }
                };
                return wrap(handler.getConditions());
            case STRING:
                handler = new GenericFieldHandler(node, fieldName) {
                    @Override
                    protected Token getToken(JsonNode node, MatchCondition matchCondition) {
                        return new Token<String>(node.asText(), matchCondition);
                    }
                };
                return wrap(handler.getConditions());
        }
        throw new MalformedQueryException("Field '" + fieldName + "' does not seem to be supported.");
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

    private List<ClauseNode> handleDateTime(String fieldName, ObjectNode node) throws MalformedQueryException {
        String rawValue = node.get("value").asText();
        try {
            //Get DateFormat from Schema and parse value according to format
            Date date = ((DateTimeIndex) schema.getFieldIndex(fieldName)).getDateFormat().parse(rawValue);
            String comparator = node.get("condition").asText();
            MatchCondition matchCondition = getMatchCondition(comparator);
            Token<Long> token = new Token<Long>(date.getTime(), matchCondition);
            List<ClauseNode> conditions = new ArrayList<ClauseNode>();
            conditions.add(new Condition(fieldName, token));
            return conditions;
        } catch (ParseException e) {
            throw new MalformedQueryException("Date could not be parsed: '" + rawValue + "'", e);
        }
    }

    public List<ClauseNode> handleFullText(String fieldName, ObjectNode node) throws TokenizerException {
        String query = node.get("value").asText();
        Analyzer analyzer = ((FullTextIndex) schema.getFieldIndex(fieldName)).getAnalyzer();

        List<ClauseNode> conditions = new ArrayList<ClauseNode>();
        for (Token token : analyzer.tokenize(query)) {
            conditions.add(new Condition(fieldName, token));
        }
        return conditions;
    }

    public List<ClauseNode> handleGeoDistance(String fieldName, ObjectNode node) {
        ObjectNode location = (ObjectNode) node.get("value");
        List<ClauseNode> conditions = new ArrayList<ClauseNode>();

        GeoPerimeter geo = new GeoPerimeter(
                location.get("lat").asDouble(),
                location.get("lng").asDouble(),
                location.get("distance").asInt()
        );
        conditions.add(new Condition(fieldName, new Token<GeoPerimeter>(geo)));

        return conditions;
    }

    private ClauseNode wrap(List<ClauseNode> conditions) {
        if (conditions.size() == 1) {
            return conditions.get(0);
        } else {
            return new Clause(Clause.ClauseType.AND, conditions);
        }
    }
}
