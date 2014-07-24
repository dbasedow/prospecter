package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.index.FieldIndex;

public interface Schema {
    public void addFieldIndex(String fieldName, FieldIndex index);

    public QueryPosting[] matchField(String fieldName) throws UndefinedIndexFieldException;

}
