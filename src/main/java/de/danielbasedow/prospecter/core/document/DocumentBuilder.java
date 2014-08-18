package de.danielbasedow.prospecter.core.document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.analysis.TokenizerException;
import de.danielbasedow.prospecter.core.geo.LatLng;
import de.danielbasedow.prospecter.core.index.DateTimeIndex;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.schema.Schema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Build Document instances from JSON representations
 */
public class DocumentBuilder {
    private static final ObjectMapper mapper = new ObjectMapper();

    protected Schema schema;

    /**
     * @param schema Schema to use
     */
    public DocumentBuilder(Schema schema) {
        this.schema = schema;
    }

    /**
     * Get Document instance from a JSON representation
     *
     * @param json raw JSON
     * @return Document instance
     * @throws MalformedDocumentException
     */
    public Document build(String json) throws MalformedDocumentException {
        Document doc = new Document();
        try {
            ObjectNode root = (ObjectNode) mapper.readTree(json);

            Iterator<Map.Entry<String, JsonNode>> iterator = root.fields();
            while (iterator.hasNext()) {
                Map.Entry<String, JsonNode> entry = iterator.next();
                String fieldName = entry.getKey();
                doc.addField(fieldName, handleField(entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            throw new MalformedDocumentException("Error parsing the JSON string", e);
        } catch (TokenizerException e) {
            throw new MalformedDocumentException("Error tokenizing field content", e);
        }
        return doc;
    }

    /**
     * build method dispatches every encountered field in JSON to this method.
     *
     * @param fieldName name of the field
     * @param node      ObjectNode representing the field
     * @return Field instance
     * @throws MalformedDocumentException
     * @throws TokenizerException
     */
    private Field handleField(String fieldName, JsonNode node) throws MalformedDocumentException, TokenizerException {
        FieldIndex index = schema.getFieldIndex(fieldName);
        if (index == null) {
            throw new MalformedDocumentException("The document field '" + fieldName + "' doesn't exist in schema");
        }

        switch (index.getFieldType()) {
            case FULL_TEXT:
                return handleFullTextField(fieldName, node);
            case INTEGER:
                return handleIntegerField(fieldName, node);
            case GEO_DISTANCE:
                return handleGeoDistanceField(fieldName, node);
            case DATE_TIME:
                return handleDateTimeField(fieldName, node);
            case STRING:
                return handleStringField(fieldName, node);
            case DOUBLE:
                return handleDoubleField(fieldName, node);
            case LONG:
                return handleLongField(fieldName, node);
            default:
                throw new NotImplementedException();
        }
    }

    private Field handleLongField(String fieldName, JsonNode node) {
        List<Token> tokens = new ArrayList<Token>();
        if (node.getNodeType() == JsonNodeType.ARRAY) {
            Iterator<JsonNode> iterator = ((ArrayNode) node).elements();
            while (iterator.hasNext()) {
                JsonNode subNode = iterator.next();
                if (subNode.getNodeType() == JsonNodeType.NUMBER) {
                    tokens.add(new Token<Long>(subNode.asLong()));
                }
            }
        } else if (node.getNodeType() == JsonNodeType.NUMBER) {
            tokens.add(new Token<Long>(node.asLong()));
        }
        return new Field(fieldName, tokens);
    }

    private Field handleDoubleField(String fieldName, JsonNode node) {
        List<Token> tokens = new ArrayList<Token>();
        if (node.getNodeType() == JsonNodeType.ARRAY) {
            Iterator<JsonNode> iterator = ((ArrayNode) node).elements();
            while (iterator.hasNext()) {
                JsonNode subNode = iterator.next();
                if (subNode.getNodeType() == JsonNodeType.NUMBER) {
                    tokens.add(new Token<Double>(subNode.asDouble()));
                }
            }
        } else if (node.getNodeType() == JsonNodeType.NUMBER) {
            tokens.add(new Token<Double>(node.asDouble()));
        }
        return new Field(fieldName, tokens);
    }

    private Field handleStringField(String fieldName, JsonNode node) {
        List<Token> tokens = new ArrayList<Token>();
        if (node.getNodeType() == JsonNodeType.ARRAY) {
            Iterator<JsonNode> iterator = ((ArrayNode) node).elements();
            while (iterator.hasNext()) {
                JsonNode subNode = iterator.next();
                if (subNode.getNodeType() == JsonNodeType.STRING) {
                    tokens.add(new Token<String>(subNode.asText()));
                }
            }
        } else if (node.getNodeType() == JsonNodeType.STRING) {
            tokens.add(new Token<String>(node.asText()));
        }
        return new Field(fieldName, tokens);
    }

    /**
     * GeoDistanceIndex backed fields are handled here
     *
     * @param fieldName name of the field
     * @param node      JsonNode representing the field content. This can be an array or an object
     * @return Field instance
     */
    private Field handleGeoDistanceField(String fieldName, JsonNode node) {
        List<Token> tokens = new ArrayList<Token>();
        if (node.getNodeType() == JsonNodeType.ARRAY) {
            Iterator<JsonNode> iterator = ((ArrayNode) node).elements();
            while (iterator.hasNext()) {
                JsonNode subNode = iterator.next();
                if (subNode.getNodeType() == JsonNodeType.OBJECT) {
                    LatLng latLng = new LatLng(subNode.get("lat").asDouble(), subNode.get("lng").asDouble());
                    Token<LatLng> token = new Token<LatLng>(latLng);
                    tokens.add(token);
                }
            }
        } else if (node.getNodeType() == JsonNodeType.OBJECT) {
            LatLng latLng = new LatLng(node.get("lat").asDouble(), node.get("lng").asDouble());
            Token<LatLng> token = new Token<LatLng>(latLng);
            tokens.add(token);
        }
        return new Field(fieldName, tokens);
    }

    /**
     * IntegerIndex backed fields are handled here
     *
     * @param fieldName name of the field
     * @param node      JsonNode representing the field content. This can be an array or a number
     * @return Field instance
     */
    private Field handleIntegerField(String fieldName, JsonNode node) {
        List<Token> tokens = new ArrayList<Token>();
        if (node.getNodeType() == JsonNodeType.ARRAY) {
            Iterator<JsonNode> iterator = ((ArrayNode) node).elements();
            while (iterator.hasNext()) {
                JsonNode subNode = iterator.next();
                if (subNode.getNodeType() == JsonNodeType.NUMBER) {
                    tokens.add(new Token<Integer>(subNode.asInt()));
                }
            }
        } else if (node.getNodeType() == JsonNodeType.NUMBER) {
            tokens.add(new Token<Integer>(node.asInt()));
        }
        return new Field(fieldName, tokens);
    }

    /**
     * DateTimeIndex backed fields are handled here
     *
     * @param fieldName name of the field
     * @param node      JsonNode representing the field content. This can be an array or a formatted date
     * @return Field instance
     */
    private Field handleDateTimeField(String fieldName, JsonNode node) {
        List<Token> tokens = new ArrayList<Token>();
        DateFormat dateFormat = ((DateTimeIndex) schema.getFieldIndex(fieldName)).getDateFormat();
        try {
            if (node.getNodeType() == JsonNodeType.ARRAY) {
                Iterator<JsonNode> iterator = ((ArrayNode) node).elements();
                while (iterator.hasNext()) {
                    JsonNode subNode = iterator.next();
                    if (subNode.getNodeType() == JsonNodeType.STRING) {
                        String value = subNode.asText();
                        tokens.add(new Token<Long>(dateFormat.parse(value).getTime()));
                    }
                }
            } else if (node.getNodeType() == JsonNodeType.STRING) {
                String value = node.asText();
                tokens.add(new Token<Long>(dateFormat.parse(value).getTime()));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Field(fieldName, tokens);
    }

    /**
     * FullTextIndex backed fields are handled here
     *
     * @param fieldName name of the field
     * @param node      JsonNode representing the field content. This has to be a text node
     * @return Field instance
     * @throws TokenizerException
     */
    private Field handleFullTextField(String fieldName, JsonNode node) throws TokenizerException {
        Analyzer analyzer = ((FullTextIndex) schema.getFieldIndex(fieldName)).getAnalyzer();
        List<Token> tokens = analyzer.tokenize(node.asText(), true);
        return new Field(fieldName, tokens);
    }
}
