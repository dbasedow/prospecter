package de.danielbasedow.prospecter.benchmark;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.document.MalformedDocumentException;
import de.danielbasedow.prospecter.core.schema.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.List;

public class Application {
    public Application() {

    }

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

    public static Document buildDoc(DocumentBuilder builder, String query) {
        JsonStringEncoder encoder = JsonStringEncoder.getInstance();
        String json = "{\"textField\": \"" + new String(encoder.quoteAsString(query)) + "\"}";
        try {
            return builder.build(json);
        } catch (MalformedDocumentException e) {
            e.printStackTrace();
        }
        return new Document();
    }


    public static void main(String[] args) {
        try {
            Schema schema = buildSchema(args[0]);

            String line;
            BufferedReader testDoc = new BufferedReader(new FileReader(new File(args[2])));
            String queryStr = "";
            while ((line = testDoc.readLine()) != null) {
                queryStr = queryStr + " " + line;
            }
            //Document testDocument = buildDoc(schema.getDocumentBuilder(), queryStr);
            BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
            long i = 0;
            while ((line = br.readLine()) != null) {
                String[] columns = line.trim().split("\\t");
                i++;
                if (columns.length == 3) {
                    schema.addQuery(buildJsonQuery(columns[2].trim(), i));
                }
                if (i % 10000 == 0) {
                    System.out.print(i);
                    System.out.print(",");
                    long sumTime = 0;
                    for (int p = 0; p < 10; p++) {
                        sumTime += testPerformance(schema, queryStr);
                    }
                    System.out.println(sumTime / 10.0);
                }
            }
            br.close();

            /*
            //Insert some tweets here
            System.out.println("run tweets");
            runMatching(schema, "");
            runMatching(schema, "");
            runMatching(schema, "");
            runMatching(schema, "");
            */
            runMatching(schema, queryStr);
            runMatching(schema, queryStr);
            schema.close();
            printVMStats();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static long testPerformance(Schema schema, String doc) {
        long start = (new Date()).getTime();
        Document document = buildDoc(schema.getDocumentBuilder(), doc);
        Matcher matcher = schema.matchDocument(document);
        List<Query> queries = matcher.getMatchedQueries();
        long end = (new Date()).getTime();
        //System.out.print(queries.size());
        //System.out.print(" ");
        //System.out.println(end - start);
        return end - start;
    }

    public static void runMatching(Schema schema, String queryStr) {
        System.out.println("start matching " + (new Date()).getTime());
        Document doc = buildDoc(schema.getDocumentBuilder(), queryStr);
        Matcher matcher = schema.matchDocument(doc);
        System.out.println("matching done " + (new Date()).getTime());

        System.out.println("start testing " + (new Date()).getTime());
        List<Query> queries = matcher.getMatchedQueries();
        System.out.println("testing done " + (new Date()).getTime());

        System.out.println("Queries returned: " + Integer.toString(queries.size()));

    }

    public static void printVMStats() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
        runtime.gc();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
    }
}
