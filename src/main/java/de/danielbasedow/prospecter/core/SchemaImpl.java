package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.document.Document;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.document.FieldIterator;
import de.danielbasedow.prospecter.core.index.FieldIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
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
    public ArrayList<QueryPosting> matchField(String fieldIndexName, Field field) throws UndefinedIndexFieldException {
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
}
