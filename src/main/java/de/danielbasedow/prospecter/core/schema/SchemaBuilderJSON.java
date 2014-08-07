package de.danielbasedow.prospecter.core.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.index.GeoDistanceIndex;
import de.danielbasedow.prospecter.core.index.IntegerIndex;

public class SchemaBuilderJSON implements SchemaBuilder {
    private String rawJSON;
    private ObjectMapper objectMapper;
    private Schema schema;

    public SchemaBuilderJSON(String json) {
        objectMapper = new ObjectMapper();
        rawJSON = json;
        schema = new SchemaImpl();
    }

    private void parseJSON() throws SchemaConfigurationError {
        try {
            ObjectNode root = (ObjectNode) objectMapper.readTree(rawJSON);
            ArrayNode fields = (ArrayNode) root.get("fields");
            for (JsonNode node : fields) {
                FieldIndex index = buildField(node);
                schema.addFieldIndex(index.getName(), index);
            }
        } catch (Exception e) {
            throw new SchemaConfigurationError();
        }
    }

    protected FieldIndex buildField(JsonNode node) {
        FieldIndex index = null;
        String type = node.get("type").asText();
        String name = node.get("name").asText();

        if ("FullText".equals(type)) {
            index = new FullTextIndex(name);
        } else if ("Integer".equals(type)) {
            index = new IntegerIndex(name);
        } else if ("GeoDistance".equals(type)) {
            index = new GeoDistanceIndex(name);
        }

        return index;
    }

    @Override
    public Schema getSchema() throws SchemaConfigurationError {
        parseJSON();
        return schema;
    }
}
