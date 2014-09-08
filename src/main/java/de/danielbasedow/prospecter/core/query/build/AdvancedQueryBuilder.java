package de.danielbasedow.prospecter.core.query.build;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.MalformedQueryException;
import de.danielbasedow.prospecter.core.query.*;
import de.danielbasedow.prospecter.core.query.Condition;
import de.danielbasedow.prospecter.core.schema.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AdvancedQueryBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryBuilder.class);
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
        List<de.danielbasedow.prospecter.core.query.Condition> conditions = new ArrayList<Condition>();
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(json);
            Integer queryId = root.get("id").asInt();

            return new Query(queryId, conditions);
        } catch (Exception e) {
            throw new MalformedQueryException("Error parsing query", e);
        }
    }
}
