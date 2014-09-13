package de.danielbasedow.prospecter.core;


import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;
import junit.framework.TestCase;

public class OrQueryTest extends TestCase {
    protected final String schemaJSON = "{\n" +
            "    \"fields\": {\n" +
            "        \"price\": {\n" +
            "            \"type\": \"Integer\"\n" +
            "        },\n" +
            "        \"category\": {\n" +
            "            \"type\": \"String\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    public void test() throws SchemaConfigurationError, MalformedQueryException, UndefinedIndexFieldException {
        SchemaBuilder schemaBuilder = new SchemaBuilderJSON(schemaJSON);
        Schema schema = schemaBuilder.getSchema();

        schema.addQuery("{\n" +
                "    \"id\": 123456,\n" +
                "    \"query\": {\n" +
                "        \"or\": [\n" +
                "            {\n" +
                "                \"field\": \"category\",\n" +
                "                \"condition\": \"eq\",\n" +
                "                \"value\": \"bargain\"\n" +
                "            },\n" +
                "            {\n" +
                "                \"field\": \"price\",\n" +
                "                \"condition\": \"lt\",\n" +
                "                \"value\": 500000\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}");

        Document doc = schema.getDocumentBuilder().build("{\"category\": \"foo\"}");
        Matcher matcher = schema.matchDocument(doc);
        assertEquals(0, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"category\": \"bargain\"}");
        matcher = schema.matchDocument(doc);
        assertEquals(1, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"price\": 500001}");
        matcher = schema.matchDocument(doc);
        assertEquals(0, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"price\": 499999}");
        matcher = schema.matchDocument(doc);
        assertEquals(1, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"price\": 499999, \"category\": \"bargain\"}");
        matcher = schema.matchDocument(doc);
        assertEquals(1, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"price\": 500001, \"category\": \"bargain\"}");
        matcher = schema.matchDocument(doc);
        assertEquals(1, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"price\": 500001, \"category\": \"foo\"}");
        matcher = schema.matchDocument(doc);
        assertEquals(0, matcher.getMatchedQueries().size());

        doc = schema.getDocumentBuilder().build("{\"price\": 499999, \"category\": \"foo\"}");
        matcher = schema.matchDocument(doc);
        assertEquals(1, matcher.getMatchedQueries().size());
    }
}
