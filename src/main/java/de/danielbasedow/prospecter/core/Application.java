package de.danielbasedow.prospecter.core;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.document.MalformedDocumentException;
import de.danielbasedow.prospecter.core.schema.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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
            QueryBuilder queryBuilder = schema.getQueryBuilder();

            BufferedReader br = new BufferedReader(new FileReader(new File(args[1])));
            String line;
            long i = 0;
            System.out.println("start indexing " + (new Date()).getTime());
            while ((line = br.readLine()) != null) {
                String[] columns = line.trim().split("\\t");
                i++;
                if (columns.length == 3) {
                    Query q = queryBuilder.buildFromJSON(buildJsonQuery(columns[2].trim(), i));
                    schema.addQuery(q);
                }
            }
            br.close();
            System.out.println("indexing done " + (new Date()).getTime());
            System.out.println(i);
            BufferedReader testDoc = new BufferedReader(new FileReader(new File(args[2])));
            String queryStr = "";
            while ((line = testDoc.readLine()) != null) {
                queryStr = queryStr + " " + line;
            }
            System.out.println("start matching " + (new Date()).getTime());
            Document doc = buildDoc(schema.getDocumentBuilder(), queryStr);
            Matcher matcher = schema.getMatcher();
            schema.matchDocument(doc, matcher);
            System.out.println("matching done " + (new Date()).getTime());

            System.out.println("start testing " + (new Date()).getTime());
            List<Query> queries = matcher.getMatchedQueries();
            System.out.println("testing done " + (new Date()).getTime());

            System.out.println("Queries returned: " + Integer.toString(queries.size()));

            printVMStats();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UndefinedIndexFieldException e) {
            e.printStackTrace();
        } catch (SchemaConfigurationError schemaConfigurationError) {
            schemaConfigurationError.printStackTrace();
        } catch (MalformedQueryException e) {
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
