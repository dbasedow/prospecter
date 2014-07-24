package de.danielbasedow.prospecter.core;

import com.google.inject.Inject;

import java.util.HashMap;

public class QueryManagerImpl implements QueryManager {
    protected HashMap<Long, Query> queries;

    public QueryManagerImpl() {
        this.queries = new HashMap<Long, Query>();
    }

    @Override
    public void addQuery(Query query) {
        queries.put(query.getQueryId(), query);
    }

}
