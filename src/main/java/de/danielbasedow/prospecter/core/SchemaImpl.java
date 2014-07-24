package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.index.FieldIndex;

import java.util.concurrent.ConcurrentHashMap;

public class SchemaImpl implements Schema {
    protected ConcurrentHashMap<String, FieldIndex> indices;

    public SchemaImpl() {
        indices = new ConcurrentHashMap<String, FieldIndex>();
    }

    @Override
    public void addFieldIndex(String fieldName, FieldIndex index) {
        indices.put(fieldName, index);
    }

    @Override
    public QueryPosting[] matchField(String fieldName) throws UndefinedIndexFieldException {
        if (!indices.containsKey(fieldName)) {
            throw new UndefinedIndexFieldException("No field named '" + fieldName + "'");
        }
        return indices.get(fieldName).match();
    }
}
