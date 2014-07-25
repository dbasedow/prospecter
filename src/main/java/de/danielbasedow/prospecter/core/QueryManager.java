package de.danielbasedow.prospecter.core;

public interface QueryManager {
    public void addQuery(Query query);

    public Query getQuery(Long queryId);
}
