package de.danielbasedow.prospecter.benchmark;

import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.MalformedDocumentException;
import de.danielbasedow.prospecter.core.query.Query;
import de.danielbasedow.prospecter.core.query.build.AdvancedQueryBuilder;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;

import java.util.Date;

public class GeoDistanceIndexTest {
    protected static final String schemaJSON = "{\n" +
            "    \"fields\": {\n" +
            "        \"location\": {\n" +
            "            \"type\": \"GeoDistance\"\n" +
            "        }\n" +
            "    }\n" +
            "}";

    protected static final String documentJSON = "{\n" +
            "        \"location\": {\n" +
            "            \"lat\": 53.55,\n" +
            "            \"lng\": 10\n" +
            "        }\n" +
            "}";

    public static void main(String[] args) {
        try {
            SchemaBuilder schemaBuilder = new SchemaBuilderJSON(schemaJSON);
            Schema schema = schemaBuilder.getSchema();
            System.out.print((new Date()).getTime());
            System.out.println(" start filling index");
            fillIndex(schema, 1000000, 5.5, 15.0, 55.0, 47.0, 100000);
            System.out.print((new Date()).getTime());
            System.out.println(" done filling index");
            System.out.print((new Date()).getTime());
            System.out.println(" start matching");
            Document doc = schema.getDocumentBuilder().build(documentJSON);

            Matcher matcher = schema.matchDocument(doc);
            System.out.print((new Date()).getTime());
            System.out.println(" done matching");
            System.out.println("Matched: " + Integer.toString(matcher.getMatchedQueries().size()));
        } catch (SchemaConfigurationError e) {
            e.printStackTrace();
        } catch (MalformedDocumentException e) {
            e.printStackTrace();
        }

        printVMStats();
    }

    private static void fillIndex(Schema schema, int count, double west, double east, double north, double south, int maxDistance) {
        AdvancedQueryBuilder queryBuilder = schema.getQueryBuilder();
        for (int i = 0; i < count; i++) {
            String json = getRandomQuery(west, east, north, south, maxDistance, (long) i);
            try {
                Query q = queryBuilder.buildFromJSON(json);
                schema.addQuery(q);
            } catch (MalformedQueryException e) {
                e.printStackTrace();
            } catch (UndefinedIndexFieldException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getRandomQuery(double west, double east, double north, double south, int maxDistance, Long queryId) {
        double latitude = Math.random() * (north - south) + south;
        double longitude = Math.random() * (east - west) + west;
        return "{\n" +
                "\"id\": " + String.valueOf(queryId) + ",\n" +
                "\"query\": {" +
                "   \"conditions\": [\n" +
                "            {\n" +
                "                \"field\": \"location\",\n" +
                "                \"condition\": \"radius\",\n" +
                "                \"value\": {\n" +
                "                    \"lat\": " + String.valueOf(latitude) + ",\n" +
                "                    \"lng\": " + String.valueOf(longitude) + ",\n" +
                "                    \"distance\": " + String.valueOf((int) (Math.random() * maxDistance)) + "\n" +
                "                }\n" +
                "            }" +
                "        ]" +
                "    }" +
                "}";
    }

    public static void printVMStats() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
        runtime.gc();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
    }
}
