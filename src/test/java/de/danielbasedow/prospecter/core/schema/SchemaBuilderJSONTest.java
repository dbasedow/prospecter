package de.danielbasedow.prospecter.core.schema;

import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.index.IntegerIndex;
import junit.framework.TestCase;

public class SchemaBuilderJSONTest extends TestCase {
    public void testValidJSON() {
        String json = "{" +
                "    \"fields\": {" +
                "        \"textField\": {" +
                "            \"type\": \"FullText\"," +
                "            \"options\": {" +
                "                \"analyzer\": \"de.danielbasedow.prospecter.core.analysis.LuceneStandardAnalyzer\"" +
                "            }" +
                "        }," +
                "        \"price\": {" +
                "            \"type\": \"Integer\"" +
                "        }" +
                "    }" +
                "}";
        Schema schema;
        try {
            SchemaBuilder builder = new SchemaBuilderJSON(json);
            schema = builder.getSchema();
            assertEquals(2, schema.getFieldCount());
            assertTrue(schema.getFieldIndex("price") instanceof IntegerIndex);
            assertTrue(schema.getFieldIndex("textField") instanceof FullTextIndex);
        } catch (SchemaConfigurationError e) {
            assertTrue(false);
        }

    }
}
