package de.danielbasedow.prospecter.core.query;

import de.danielbasedow.prospecter.core.query.Query;

import java.util.HashMap;

public class QueryManager {
    protected HashMap<Integer, Query> queries;

    public QueryManager() {
        this.queries = new HashMap<Integer, Query>();
    }

    public void addQuery(Query query) {
        queries.put(query.getQueryId(), query);
    }

    public Query getQuery(Integer queryId) {
        return queries.get(queryId);
    }

    public void deleteQuery(Integer queryId) {
        queries.remove(queryId);
    }
}
