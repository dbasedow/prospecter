package de.danielbasedow.prospecter.core.document;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class FieldIterator implements Iterator<Field> {
    private HashMap<String, Field> fields;
    private int index;
    private String[] keys;

    public FieldIterator(HashMap<String, Field> fields) {
        this.fields = fields;
        Set<String> keySet = fields.keySet();
        keys = keySet.toArray(new String[keySet.size()]);
        index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < keys.length;
    }

    @Override
    public Field next() {
        return fields.get(keys[index++]);
    }

    @Override
    public void remove() {
        throw new NotImplementedException();
    }
}
