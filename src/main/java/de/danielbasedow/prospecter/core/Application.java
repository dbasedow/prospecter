package de.danielbasedow.prospecter.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaImpl;

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

    public static Schema buildSchema() {
        Schema schema = new SchemaImpl();
        schema.addFieldIndex("_all", new FullTextIndex("_all"));
        return schema;
    }

    public static Document buildDoc(DocumentBuilder builder, String query) {
        HashMap<String, String> rawFields = new HashMap<String, String>();
        rawFields.put("_all", query);
        return builder.build(rawFields);
    }


    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ProspecterModule());
        QueryManager queryManager = injector.getInstance(QueryManager.class);
        QueryBuilder queryBuilder = injector.getInstance(QueryBuilder.class);
        Schema schema = buildSchema();
        DocumentBuilder docBuilder = injector.getInstance(DocumentBuilder.class);

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
            String line;
            long i = 0;
            System.out.println("start indexing " + (new Date()).getTime());
            while ((line = br.readLine()) != null) {
                String[] columns = line.trim().split("\\t");
                i++;
                if (columns.length == 3) {
                    Query q = queryBuilder.buildFromString(i, columns[2].trim());
                    queryManager.addQuery(q);
                    schema.addPostingsToField("_all", q.getPostings());
                }
            }
            br.close();
            System.out.println("indexing done " + (new Date()).getTime());
            System.out.println(i);
            BufferedReader testDoc = new BufferedReader(new FileReader(new File(args[1])));
            String queryStr = "";
            while ((line = testDoc.readLine()) != null) {
                queryStr = queryStr + " " + line;
            }
            //Document doc = buildDoc(docBuilder, "yahoo search is the part of the log");
            System.out.println("start matching " + (new Date()).getTime());
            Document doc = buildDoc(docBuilder, queryStr);
            Matcher matcher = injector.getInstance(Matcher.class);
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
