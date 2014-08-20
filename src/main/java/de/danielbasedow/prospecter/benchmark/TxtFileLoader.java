package de.danielbasedow.prospecter.benchmark;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class TxtFileLoader {

    public static Schema buildSchema(String fileName) throws SchemaConfigurationError {
        SchemaBuilder schemaBuilder = new SchemaBuilderJSON(new File(fileName));
        return schemaBuilder.getSchema();
    }

    public static String buildJsonQuery(String queryString, long id) {
        JsonStringEncoder encoder = JsonStringEncoder.getInstance();
        return "{" +
                "\"id\": " + String.valueOf(id) + "," +
                "\"query\": {" +
                "\"conditions\": [" +
                "{" +
                "\"field\": \"textField\"," +
                "\"condition\": \"match\"," +
                "\"value\": \"" + new String(encoder.quoteAsString(queryString)) + "\"" +
                "}" +
                "]" +
                "}" +
                "}";
    }


    public static void main(String[] args) {
        try {
            Schema schema = buildSchema(args[0]);

            String line;
            BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
            long i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                schema.addQuery(buildJsonQuery(line.trim(), i));
                if (i % 10000 == 0) {
                    System.out.println(i);
                }
            }
            br.close();
            schema.close();
            printVMStats();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void printVMStats() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
        runtime.gc();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
    }
}
