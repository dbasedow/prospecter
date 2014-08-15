package de.danielbasedow.prospecter.core.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.index.*;
import de.danielbasedow.prospecter.core.persistence.MapDBStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

/**
 * Build a Schema from a JSON configuration.
 */
public class SchemaBuilderJSON implements SchemaBuilder {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaBuilderJSON.class);

    private Schema schema;
    private ObjectNode root;

    /**
     * Build Schema from JSON String
     *
     * @param json raw JSON string
     * @throws SchemaConfigurationError
     */
    public SchemaBuilderJSON(String json) throws SchemaConfigurationError {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LOGGER.debug("parsing json");
            root = (ObjectNode) objectMapper.readTree(json);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new SchemaConfigurationError("Could not parse schema JSON.");
        }
        schema = new SchemaImpl();
    }

    /**
     * Build Schema from a JSON file
     *
     * @param file file to read JSON from
     * @throws SchemaConfigurationError
     */
    public SchemaBuilderJSON(File file) throws SchemaConfigurationError {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            LOGGER.debug("parsing schema " + file.getAbsoluteFile());
            root = (ObjectNode) objectMapper.readTree(file);
        } catch (IOException e) {
            LOGGER.error(e.getLocalizedMessage());
            throw new SchemaConfigurationError("Could not parse schema JSON from file " + file.getAbsoluteFile());
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
            JsonNode persistence = root.get("persistence");
            if (persistence != null && persistence.getNodeType() == JsonNodeType.OBJECT) {
                //if persistence key doesn't exist there is no persistence
                JsonNode file = persistence.get("file");
                if (file != null && file.getNodeType() == JsonNodeType.STRING) {
                    schema.setQueryStorage(new MapDBStore(new File(file.asText())));
                }
            }
        } catch (Exception e) {
            throw new SchemaConfigurationError("Could not parse JSON tree", e);
        }
        schema.init();
    }

    protected FieldIndex buildField(String fieldName, JsonNode node) throws SchemaConfigurationError {
        FieldIndex index = null;
        String type = node.get("type").asText();

        if ("FullText".equals(type)) {
            Analyzer analyzer = null;
            try {
                analyzer = getAnalyzer(node.get("options"));
            } catch (Exception e) {
                throw new SchemaConfigurationError("Couldn't create the analyzer instance.", e);
            }
            index = new FullTextIndex(fieldName, analyzer);
        } else if ("Integer".equals(type)) {
            index = new IntegerIndex(fieldName);
        } else if ("GeoDistance".equals(type)) {
            index = new GeoDistanceIndex(fieldName);
        } else if ("String".equals(type)) {
            index = new StringIndex(fieldName);
        } else if ("Long".equals(type)) {
            index = new LongIndex(fieldName);
        } else if ("Double".equals(type)) {
            index = new DoubleIndex(fieldName);
        } else if ("DateTime".equals(type)) {
            JsonNode format = node.get("format");
            DateFormat df;
            if (format != null) {
                df = new SimpleDateFormat(format.asText());
            } else {
                df = new ISO8601DateFormat();
            }
            index = new DateTimeIndex(fieldName, df);
        }

        return index;
    }

    private Analyzer getAnalyzer(JsonNode options) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String analyzerName = "de.danielbasedow.prospecter.core.analysis.LuceneStandardAnalyzer";
        if (options != null && options.getNodeType() == JsonNodeType.OBJECT) {
            JsonNode analyzerNode = options.get("analyzer");
            if (analyzerNode != null && analyzerNode.getNodeType() == JsonNodeType.STRING) {
                analyzerName = analyzerNode.asText();
            }
        }
        Class class_ = Class.forName(analyzerName);
        Method factory = class_.getMethod("make", JsonNode.class);
        return (Analyzer) factory.invoke(null, options);
    }

    @Override
    public Schema getSchema() throws SchemaConfigurationError {
        parseJSON();
        return schema;
    }
}
