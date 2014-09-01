package de.danielbasedow.prospecter.core.persistence;

public interface QueryStorage {
    public void addQuery(Integer queryId, String json);

    public java.util.Set<java.util.Map.Entry<Integer, String>> getAllQueries();

    public void deleteQuery(Integer queryId);

    public void close();

    public String getRawQuery(Integer queryId);
}
