package de.danielbasedow.prospecter.benchmark;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaBuilder;
import de.danielbasedow.prospecter.core.schema.SchemaBuilderJSON;
import de.danielbasedow.prospecter.core.schema.SchemaConfigurationError;

import java.io.*;
import java.util.Date;

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

    public static String getJsonDoc() {
        String line;
        try {
            BufferedReader testDoc = new BufferedReader(new FileReader(new File("testdoc.txt")));
            String queryStr = "";
            while ((line = testDoc.readLine()) != null) {
                queryStr = queryStr + " " + line;
            }
            JsonStringEncoder encoder = JsonStringEncoder.getInstance();
            return "{\"textField\": \"" + new String(encoder.quoteAsString(queryStr)) + "\"}";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new RuntimeException();
    }


    public static void main(String[] args) {
        try {
            Schema schema = buildSchema(args[0]);
            String doc = getJsonDoc();

            String line;
            BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
            long i = 0;
            while ((line = br.readLine()) != null) {
                i++;
                schema.addQuery(buildJsonQuery(line.trim(), i));
                if (i % 10000 == 0) {
                    measure(schema, doc, i);
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

    public static void measure(Schema schema, String jsonDoc, long queryCount) {
        long sumQueryTime = 0;
        Runtime runtime = Runtime.getRuntime();
        for (int i = 0; i < 10; i++) {
            Document doc = schema.getDocumentBuilder().build(jsonDoc);
            long startTime = new Date().getTime();
            Matcher m = schema.matchDocument(doc);
            m.getMatchedQueries();
            long endTime = new Date().getTime();
            sumQueryTime += endTime - startTime;
        }
        runtime.gc();
        double avgQueryTime = sumQueryTime / 10.0;
        System.out.println(String.valueOf(queryCount) + ";" + String.valueOf(avgQueryTime) + ";" + String.valueOf(runtime.totalMemory() - runtime.freeMemory()));
    }
}
