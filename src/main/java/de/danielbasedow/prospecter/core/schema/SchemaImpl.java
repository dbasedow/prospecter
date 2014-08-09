package de.danielbasedow.prospecter.core.schema;

import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.DocumentBuilder;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.document.FieldIterator;
import de.danielbasedow.prospecter.core.index.FieldIndex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaImpl implements Schema {
    protected ConcurrentHashMap<String, FieldIndex> indices;
    protected QueryBuilder queryBuilder;
    protected DocumentBuilder documentBuilder;
    protected QueryManager queryManager;

    public SchemaImpl() {
        indices = new ConcurrentHashMap<String, FieldIndex>();
        queryBuilder = new QueryBuilder(this);
        documentBuilder = new DocumentBuilder(this);
        queryManager = new QueryManager();
    }

    @Override
    public void addFieldIndex(String fieldName, FieldIndex index) {
        indices.put(fieldName, index);
    }

    @Override
    public List<QueryPosting> matchField(String fieldIndexName, Field field) throws UndefinedIndexFieldException {
        if (!indices.containsKey(fieldIndexName)) {
            throw new UndefinedIndexFieldException("No field named '" + fieldIndexName + "'");
        }
        return indices.get(fieldIndexName).match(field);
    }

    @Override
    public void addPostingsToField(String fieldName, HashMap<Token, QueryPosting> postings) throws UndefinedIndexFieldException {
        if (!indices.containsKey(fieldName)) {
            throw new UndefinedIndexFieldException("No field named '" + fieldName + "'");
        }
        Set<Token> termIds = postings.keySet();
        for (Token token : termIds) {
            indices.get(fieldName).addPosting(token, postings.get(token));
        }
    }

    public void addQuery(Query query) throws UndefinedIndexFieldException {
        HashMap<Condition, QueryPosting> postings = query.getPostings();
        for (Map.Entry<Condition, QueryPosting> entry : postings.entrySet()) {
            Condition condition = entry.getKey();
            QueryPosting posting = entry.getValue();

            if (!indices.containsKey(condition.getFieldName())) {
                throw new UndefinedIndexFieldException("No field named '" + condition.getFieldName() + "'");
            }

            indices.get(condition.getFieldName()).addPosting(condition.getToken(), posting);
        }
        queryManager.addQuery(query);
    }

    @Override
    public Matcher matchDocument(Document doc, Matcher matcher) {
        FieldIterator fields = doc.getFields();
        while (fields.hasNext()) {
            Field field = fields.next();
            try {
                matcher.addHits(matchField(field.getName(), field));
            } catch (UndefinedIndexFieldException e) {
                e.printStackTrace();
            }
        }
        return matcher;
    }

    @Override
    public int getFieldCount() {
        return indices.size();
    }

    @Override
    public FieldIndex getFieldIndex(String name) {
        return indices.get(name);
    }

    @Override
    public QueryBuilder getQueryBuilder() {
        return queryBuilder;
    }

    @Override
    public DocumentBuilder getDocumentBuilder() {
        return documentBuilder;
    }

    @Override
    public Matcher getMatcher() {
        return new Matcher(queryManager);
    }

    @Override
    public QueryManager getQueryManager() {
        return queryManager;
    }
}
