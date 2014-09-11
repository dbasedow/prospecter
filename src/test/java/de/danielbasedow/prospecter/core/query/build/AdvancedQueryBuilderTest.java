package de.danielbasedow.prospecter.core.query.build;

import aima.core.logic.propositional.parsing.ast.Sentence;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.danielbasedow.prospecter.core.MalformedQueryException;
import de.danielbasedow.prospecter.core.query.Query;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class AdvancedQueryBuilderTest extends TestCase {
    private AdvancedQueryBuilder builder;
    private static final ObjectMapper mapper = new ObjectMapper();

    public void setUp() {
        try {
            SchemaBuilder schemaBuilder = new SchemaBuilderJSON("{" +
                    "    \"fields\": {\n" +
                    "        \"description\": {\n" +
                    "            \"type\": \"FullText\",\n" +
                    "            \"options\": {\"stopwords\": \"predefined\"}" +
                    "        },\n" +
                    "        \"price\": {\n" +
                    "            \"type\": \"Integer\"\n" +
                    "        },\n" +
                    "        \"location\": {\n" +
                    "            \"type\": \"GeoDistance\"\n" +
                    "        }\n" +
                    "    }" +
                    "}");
            builder = new AdvancedQueryBuilder(schemaBuilder.getSchema());
        } catch (SchemaConfigurationError schemaConfigurationError) {
            schemaConfigurationError.printStackTrace();
        }
    }

    private static String getJson() {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("advanced-query-sample.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void testExtractId() {
        try {
            Query query = builder.buildFromJSON(getJson());
            assertEquals(123456, query.getQueryId().intValue());
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }
    }

    public void testTreeParsing() {
        try {
            JsonNode node = mapper.readTree(getJson());
            ClauseNode clauseNode = builder.parseNode(node.get("query"));
            assertEquals(false, clauseNode.isLeaf());
            Clause root = (Clause) clauseNode;
            assertEquals(Clause.ClauseType.AND, root.getType());

            List<ClauseNode> subClauses = root.getSubClauses();
            assertEquals(false, subClauses.get(0).isLeaf());
            assertEquals(false, subClauses.get(1).isLeaf());

            Sentence sentence = Query.getCNF(clauseNode);
            assertTrue(sentence.getNumberSimplerSentences() > 1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }
    }
}
