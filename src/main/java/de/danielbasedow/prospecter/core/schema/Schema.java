package de.danielbasedow.prospecter.core.schema;

import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.index.FieldIndex;

import java.util.HashMap;
import java.util.List;

public interface Schema {
    public void addFieldIndex(String fieldName, FieldIndex index);

    public List<QueryPosting> matchField(String fieldIndexName, Field field) throws UndefinedIndexFieldException;

    public void addPostingsToField(String fieldName, HashMap<Token, QueryPosting> postings) throws UndefinedIndexFieldException;

    public void addQuery(Query query) throws UndefinedIndexFieldException;

    public Matcher matchDocument(Document doc, Matcher matcher);

    public int getFieldCount();

    public FieldIndex getFieldIndex(String name);

    public QueryBuilder getQueryBuilder();

    public DocumentBuilder getDocumentBuilder();

    public Matcher getMatcher();

    public QueryManager getQueryManager();
}
