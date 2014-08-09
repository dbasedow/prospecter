package de.danielbasedow.prospecter.core.document;


import java.util.HashMap;

public class Document {
    protected HashMap<String, Field> fields;

    public Document() {
        fields = new HashMap<String, Field>();
    }

    public void addField(String name, Field field) {
        fields.put(name, field);
    }

    public Field getField(String name) {
        return fields.get(name);
    }

    public FieldIterator getFields() {
        return new FieldIterator(fields);
    }

}
