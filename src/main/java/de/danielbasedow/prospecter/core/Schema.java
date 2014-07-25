package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.index.FieldIndex;

import java.util.HashMap;

public interface Schema {
    public void addFieldIndex(String fieldName, FieldIndex index);

    public QueryPosting[] matchField(String fieldName) throws UndefinedIndexFieldException;

    public void addPostingsToField(String fieldName, HashMap<Integer, QueryPosting> postings) throws UndefinedIndexFieldException;

}
