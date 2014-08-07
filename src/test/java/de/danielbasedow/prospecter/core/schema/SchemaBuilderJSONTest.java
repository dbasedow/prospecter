package de.danielbasedow.prospecter.core.schema;

import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.index.IntegerIndex;
import junit.framework.TestCase;

public class SchemaBuilderJSONTest extends TestCase {
    public void testValidJSON() {
        String json = "{" +
                "    \"fields\": [" +
                "        {" +
                "            \"name\": \"textField\"," +
                "            \"type\": \"FullText\"," +
                "            \"options\": {" +
                "                \"analyzer\": \"default\"" +
                "            }" +
                "        }," +
                "        {" +
                "            \"name\": \"price\"," +
                "            \"type\": \"Integer\"" +
                "        }" +
                "    ]" +
                "}";
        SchemaBuilder builder = new SchemaBuilderJSON(json);
        Schema schema;
        try {
            schema = builder.getSchema();
            assertEquals(2, schema.getFieldCount());
            assertTrue(schema.getFieldIndex("price") instanceof IntegerIndex);
            assertTrue(schema.getFieldIndex("textField") instanceof FullTextIndex);
        } catch (SchemaConfigurationError e) {
            assertTrue(false);
        }

    }
}
