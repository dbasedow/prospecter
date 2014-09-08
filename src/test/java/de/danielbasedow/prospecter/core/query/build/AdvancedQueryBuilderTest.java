package de.danielbasedow.prospecter.core.query.build;

import de.danielbasedow.prospecter.core.MalformedQueryException;
import de.danielbasedow.prospecter.core.query.Query;
import junit.framework.TestCase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AdvancedQueryBuilderTest extends TestCase {
    private AdvancedQueryBuilder builder = new AdvancedQueryBuilder(null);

    private static String getJson() {
        try {
            InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream("advanced-query-sample.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void testExtractId() {
        try {
            Query query = builder.buildFromJSON(getJson());
            assertEquals(123456, query.getQueryId().intValue());
        } catch (MalformedQueryException e) {
            e.printStackTrace();
        }
    }
}
