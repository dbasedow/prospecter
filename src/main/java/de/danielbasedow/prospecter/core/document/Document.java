package de.danielbasedow.prospecter.core.document;


public interface Document {
    public void addField(String name, Field field);

    public Field getField(String name);

    public FieldIterator getFields();
}
