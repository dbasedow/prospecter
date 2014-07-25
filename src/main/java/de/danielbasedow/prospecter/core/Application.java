package de.danielbasedow.prospecter.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.danielbasedow.prospecter.core.index.FullTextIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Application {
    public Application() {

    }

    public static Schema buildSchema() {
        Schema schema = new SchemaImpl();
        schema.addFieldIndex("_all", new FullTextIndex("_all"));
        return schema;
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new ProspecterModule());
        QueryManager queryManager = injector.getInstance(QueryManager.class);
        QueryBuilder queryBuilder = injector.getInstance(QueryBuilder.class);
        Schema schema = buildSchema();

        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(args[0])));
            String line;
            long i = 0;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split("\\t");
                if (columns.length == 3) {
                    Query q = queryBuilder.buildFromString(i, columns[2]);
                    queryManager.addQuery(q);
                    schema.addPostingsToField("_all", q.getPostings());

                }
                i++;
            }
            br.close();
            System.out.println(i);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UndefinedIndexFieldException e) {
            e.printStackTrace();
        }
        System.out.println(injector.getInstance(TokenMapper.class).getNewTermId());
    }
}
