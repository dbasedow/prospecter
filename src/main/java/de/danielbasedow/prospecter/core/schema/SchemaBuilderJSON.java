package de.danielbasedow.prospecter.core.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.index.GeoDistanceIndex;
import de.danielbasedow.prospecter.core.index.IntegerIndex;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

public class SchemaBuilderJSON implements SchemaBuilder {
    private Schema schema;
    private ObjectNode root;

    public SchemaBuilderJSON(String json) throws SchemaConfigurationError {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            root = (ObjectNode) objectMapper.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SchemaConfigurationError();
        }
        schema = new SchemaImpl();
    }

    public SchemaBuilderJSON(File file) throws SchemaConfigurationError {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            root = (ObjectNode) objectMapper.readTree(file);
        } catch (IOException e) {
            e.printStackTrace();
            throw new SchemaConfigurationError();
        }
        schema = new SchemaImpl();
    }

    private void parseJSON() throws SchemaConfigurationError {
        try {
            ObjectNode fields = (ObjectNode) root.get("fields");
            Iterator<Map.Entry<String, JsonNode>> iterator = fields.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                FieldIndex index = buildField(entry.getKey(), entry.getValue());
                schema.addFieldIndex(index.getName(), index);
            }
        } catch (Exception e) {
            throw new SchemaConfigurationError();
        }
    }

    protected FieldIndex buildField(String fieldName, JsonNode node) {
        FieldIndex index = null;
        String type = node.get("type").asText();

        if ("FullText".equals(type)) {
            index = new FullTextIndex(fieldName);
        } else if ("Integer".equals(type)) {
            index = new IntegerIndex(fieldName);
        } else if ("GeoDistance".equals(type)) {
            index = new GeoDistanceIndex(fieldName);
        }

        return index;
    }

    @Override
    public Schema getSchema() throws SchemaConfigurationError {
        parseJSON();
        return schema;
    }
}
