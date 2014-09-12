package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;
import junit.framework.TestCase;

public class InQueryTest extends TestCase {
    private static final String query = "" +
            "{\n" +
            "    \"id\": 123456,\n" +
            "    \"query\": {\n" +
            "        \"and\": [\n" +
            "            {\n" +
            "                \"field\": \"category\",\n" +
            "                \"condition\": \"eq\",\n" +
            "                \"value\": [1, 2, 5]\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    private static final String schemaJSON = "" +
            "{\n" +
            "    \"fields\": {\n" +
            "        \"category\": {\n" +
            "            \"type\": \"Integer\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    private static final String doc1 = "" +
            "{\n" +
            "    \"category\": 1\n" +
            "}";

    private static final String doc2 = "" +
            "{\n" +
            "    \"category\": 5\n" +
            "}";

    private static final String doc3 = "" +
            "{\n" +
            "    \"category\": 6\n" +
            "}";

    private static final String doc4 = "" +
            "{\n" +
            "    \"category\": [6, 2]\n" +
            "}";

    public void testInQuery() {
        try {
            SchemaBuilder schemaBuilder = new SchemaBuilderJSON(schemaJSON);
            Schema schema = schemaBuilder.getSchema();

            schema.addQuery(query);

            Document doc = schema.getDocumentBuilder().build(doc1);
            Matcher matcher = schema.matchDocument(doc);
            assertEquals(1, matcher.getMatchedQueries().size());

            doc = schema.getDocumentBuilder().build(doc2);
            matcher = schema.matchDocument(doc);
            assertEquals(1, matcher.getMatchedQueries().size());

            doc = schema.getDocumentBuilder().build(doc3);
            matcher = schema.matchDocument(doc);
            assertEquals(0, matcher.getMatchedQueries().size());

            doc = schema.getDocumentBuilder().build(doc4);
            matcher = schema.matchDocument(doc);
            assertEquals(1, matcher.getMatchedQueries().size());
        } catch (SchemaConfigurationError schemaConfigurationError) {
            schemaConfigurationError.printStackTrace();
        } catch (UndefinedIndexFieldException e) {
            e.printStackTrace();
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }
    }
}
