package de.danielbasedow.prospecter.core.persistence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DummyQueryStorage implements QueryStorage {
    @Override
    public void addQuery(Integer queryId, String json) {
    }

    @Override
    public Set<Map.Entry<Integer, String>> getAllQueries() {
        return new HashSet<Map.Entry<Integer, String>>();
    }

    @Override
    public void deleteQuery(Integer queryId) {
    }

    @Override
    public void close() {
    }

    @Override
    public String getRawQuery(Integer queryId) {
        return null;
    }
}
