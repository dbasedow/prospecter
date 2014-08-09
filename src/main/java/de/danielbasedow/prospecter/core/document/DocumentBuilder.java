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
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.schema.Schema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.util.*;

public class DocumentBuilder {
    protected Schema schema;

    public DocumentBuilder(Schema schema) {
        this.schema = schema;
    }

    public Document build(String json) throws MalformedDocumentException {
        ObjectMapper mapper = new ObjectMapper();
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
            e.printStackTrace();
            throw new MalformedDocumentException();
        } catch (TokenizerException e) {
            e.printStackTrace();
            throw new MalformedDocumentException();
        }
        return doc;
    }

    protected Field handleField(String fieldName, JsonNode node) throws MalformedDocumentException, TokenizerException {
        FieldIndex index = schema.getFieldIndex(fieldName);
        if (index == null) {
            throw new MalformedDocumentException();
        }

        switch (index.getFieldType()) {
            case FULL_TEXT:
                return handleFullTextField(fieldName, node);
            case INTEGER:
                return handleIntegerField(fieldName, node);
            case GEO_DISTANCE:
                return handleGeoDistanceField(fieldName, node);
            default:
                throw new NotImplementedException();
        }
    }

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

    private Field handleFullTextField(String fieldName, JsonNode node) throws TokenizerException {
        Analyzer analyzer = ((FullTextIndex) schema.getFieldIndex(fieldName)).getAnalyzer();
        List<Token> tokens = analyzer.tokenize(node.asText(), true);
        return new Field(fieldName, tokens);
    }
}
