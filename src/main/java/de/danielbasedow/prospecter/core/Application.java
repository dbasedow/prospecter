package de.danielbasedow.prospecter.core;

import com.fasterxml.jackson.core.io.JsonStringEncoder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
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
        HashMap<String, String> rawFields = new HashMap<String, String>();
        rawFields.put("textField", query);
        return builder.build(rawFields);
    }


    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ProspecterModule());

        try {
            Schema schema = buildSchema(args[0]);
            QueryManager queryManager = schema.getQueryManager();
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
                    queryManager.addQuery(q);
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
            //Document doc = buildDoc(docBuilder, "yahoo search is the part of the log");
            System.out.println("start matching " + (new Date()).getTime());
            Document doc = buildDoc(schema.getDocumentBuilder(), queryStr);
            Matcher matcher = schema.getMatcher();
            schema.matchDocument(doc, matcher);

            //matcher.printResultStats();
            List<Query> queries = matcher.getMatchedQueries();
            System.out.println("matching done " + (new Date()).getTime());


            System.out.println("Queries returned: " + Integer.toString(queries.size()));
            for (Query query : queries) {
                System.out.print(query.getQueryId());
                System.out.print(",");
                //System.out.println(": " + rawQueries.get(query.getQueryId()));
            }
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

        System.out.println(injector.getInstance(TokenMapper.class).getNewTermId());
    }

    public static void printVMStats() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
        runtime.gc();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
    }
}
