package de.danielbasedow.prospecter.core.persistence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DummyQueryStorage implements QueryStorage {
    @Override
    public void addQuery(Long queryId, String json) {
    }

    @Override
    public Set<Map.Entry<Long, String>> getAllQueries() {
        return new HashSet<Map.Entry<Long, String>>();
    }

    @Override
    public void deleteQuery(Long queryId) {
    }

    @Override
    public void close() {
    }
}
