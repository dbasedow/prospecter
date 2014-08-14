package de.danielbasedow.prospecter.core.persistence;

public interface QueryStorage {
    public void addQuery(Long queryId, String json);

    public java.util.Set<java.util.Map.Entry<Long, String>> getAllQueries();

    public void close();
}
