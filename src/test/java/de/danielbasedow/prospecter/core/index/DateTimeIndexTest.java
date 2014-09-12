package de.danielbasedow.prospecter.core.index;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import de.danielbasedow.prospecter.core.MalformedQueryException;
import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.query.Query;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;
import junit.framework.TestCase;

public class DateTimeIndexTest extends TestCase {
    private static String jsonSchema = "{\n" +
            "    \"fields\": {\n" +
            "        \"dateTime\": {\n" +
            "            \"type\": \"DateTime\"\n" +
            "        }\n" +
            "    }\n" +
            "}";
    private static String jsonQuery = "{\n" +
            "    \"id\": 123456,\n" +
            "    \"query\": {\n" +
            "        \"conditions\": [\n" +
            "            {\n" +
            "                \"field\": \"dateTime\",\n" +
            "                \"condition\": \"gte\",\n" +
            "                \"value\": \"2014-08-10T19:22:51Z\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";
    private static String jsonQueryWrongDate = "{\n" +
            "    \"id\": 123456,\n" +
            "    \"query\": {\n" +
            "        \"conditions\": [\n" +
            "            {\n" +
            "                \"field\": \"dateTime\",\n" +
            "                \"condition\": \"gte\",\n" +
            "                \"value\": \"08-2014-10T19:22:51Z\"\n" +
            "            }\n" +
            "        ]\n" +
            "    }\n" +
            "}";

    public void testSchemaCreation() {
        try {
            Schema schema = new SchemaBuilderJSON(jsonSchema).getSchema();
            assertTrue(((DateTimeIndex) schema.getFieldIndex("dateTime")).getDateFormat() instanceof ISO8601DateFormat);
        } catch (SchemaConfigurationError e) {
            assertTrue(false);
        }
    }

    public void testQueryParsing() {
        try {
            Schema schema = new SchemaBuilderJSON(jsonSchema).getSchema();
            Query query = schema.getQueryBuilder().buildFromJSON(jsonQuery);
        } catch (Exception e) {
            assertTrue(false);
        }
    }

    public void testQueryParsingFail() {
        try {
            Schema schema = new SchemaBuilderJSON(jsonSchema).getSchema();
            Query query = schema.getQueryBuilder().buildFromJSON(jsonQueryWrongDate);
            assertTrue(false);
        } catch (SchemaConfigurationError schemaConfigurationError) {
            schemaConfigurationError.printStackTrace();
        } catch (MalformedQueryException e) {
            assertTrue(true);
        }
    }

    public void testMatching() {
        try {
            Schema schema = new SchemaBuilderJSON(jsonSchema).getSchema();
            Query query = schema.getQueryBuilder().buildFromJSON(jsonQuery);
            schema.addQuery(query);
            DocumentBuilder builder = schema.getDocumentBuilder();

            Matcher matcher;
            //EQUAL
            Document doc = builder.build("{\"dateTime\": \"2014-08-10T19:22:51Z\"}");
            matcher = schema.matchDocument(doc);
            assertEquals(1, matcher.getMatchedQueries().size());

            //GREATER
            doc = builder.build("{\"dateTime\": \"2014-08-10T19:23:00Z\"}");
            matcher = schema.matchDocument(doc);
            assertEquals(1, matcher.getMatchedQueries().size());

            //LESS
            doc = builder.build("{\"dateTime\": \"2014-08-10T19:22:50Z\"}"); // one second less
            matcher = schema.matchDocument(doc);
            assertEquals(0, matcher.getMatchedQueries().size()); //date has to be GREATER or EQUAL the value in query
        } catch (Exception e) {
            assertTrue(false);
        }
    }
}
