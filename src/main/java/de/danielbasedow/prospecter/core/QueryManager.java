package de.danielbasedow.prospecter.core;

import java.util.HashMap;

public class QueryManager {
    protected HashMap<Long, Query> queries;

    public QueryManager() {
        this.queries = new HashMap<Long, Query>();
    }

    public void addQuery(Query query) {
        queries.put(query.getQueryId(), query);
    }

    public Query getQuery(Long queryId) {
        return queries.get(queryId);
    }

}
