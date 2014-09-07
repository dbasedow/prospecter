package de.danielbasedow.prospecter.core.document;


import java.util.HashMap;

/**
 * Representation of a document that should be matched against the index. A Document is made up of one or more
 * Fields accessed by a field name.
 * <p/>
 * Documents are usually created by a DocumentBuilder instance.
 */
public class Document {
    protected final HashMap<String, Field> fields;

    public Document() {
        fields = new HashMap<String, Field>();
    }

    /**
     * Add a Field to the Document
     *
     * @param name  name of the field
     * @param field Field instance
     */
    public void addField(String name, Field field) {
        fields.put(name, field);
    }

    /**
     * Gets a Field instance by it's field name
     *
     * @param name name of the field
     * @return Field instance
     */
    public Field getField(String name) {
        return fields.get(name);
    }

    /**
     * Get Iterator over all fields in this Document
     *
     * @return iterator over all available fields
     */
    public FieldIterator getFields() {
        return new FieldIterator(fields);
    }

}
