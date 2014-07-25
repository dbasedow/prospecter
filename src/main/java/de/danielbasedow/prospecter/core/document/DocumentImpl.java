package de.danielbasedow.prospecter.core.document;

import java.util.HashMap;

public class DocumentImpl implements Document {
    protected HashMap<String, Field> fields;

    public DocumentImpl() {
        fields = new HashMap<String, Field>();
    }

    @Override
    public void addField(String name, Field field) {
        fields.put(name, field);
    }

    @Override
    public Field getField(String name) {
        return fields.get(name);
    }

    @Override
    public FieldIterator getFields() {
        return new FieldIterator(fields);
    }
}
