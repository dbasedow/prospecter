package de.danielbasedow.prospecter.core.schema;

import de.danielbasedow.prospecter.core.*;
import de.danielbasedow.prospecter.core.document.*;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.persistence.QueryStorage;
import de.danielbasedow.prospecter.core.query.Condition;
import de.danielbasedow.prospecter.core.query.Query;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.build.QueryBuilder;
import gnu.trove.list.array.TLongArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaImpl implements Schema {
    private static final Logger LOGGER = LoggerFactory.getLogger(SchemaImpl.class);

    protected final ConcurrentHashMap<String, FieldIndex> indices;
    protected final QueryBuilder queryBuilder;
    protected final DocumentBuilder documentBuilder;
    protected final QueryManager queryManager;
    protected QueryStorage queryStorage;
    private boolean writeNewQueries;

    public SchemaImpl() {
        indices = new ConcurrentHashMap<String, FieldIndex>();
        queryBuilder = new QueryBuilder(this);
        documentBuilder = new DocumentBuilder(this);
        queryManager = new QueryManager();
    }

    @Override
    public void addFieldIndex(String fieldName, FieldIndex index) throws SchemaConfigurationError {
        if (indices.containsKey(fieldName)) {
            throw new SchemaConfigurationError("Field '" + fieldName + "' is defined more than once!");
        }
        indices.put(fieldName, index);
    }

    @Override
    public TLongArrayList matchField(String fieldIndexName, Field field) throws UndefinedIndexFieldException {
        if (!indices.containsKey(fieldIndexName)) {
            throw new UndefinedIndexFieldException("No field named '" + fieldIndexName + "'");
        }
        return indices.get(fieldIndexName).match(field);
    }

    public void addQuery(Query query) throws UndefinedIndexFieldException {
        Map<Condition, Long> postings = query.getPostings();
        for (Map.Entry<Condition, Long> entry : postings.entrySet()) {
            Condition condition = entry.getKey();
            Long posting = entry.getValue();

            if (!indices.containsKey(condition.getFieldName())) {
                throw new UndefinedIndexFieldException("No field named '" + condition.getFieldName() + "'");
            }

            indices.get(condition.getFieldName()).addPosting(condition.getToken(), posting);
        }
        queryManager.addQuery(query);
    }

    @Override
    public void addQuery(String json) throws UndefinedIndexFieldException, MalformedQueryException {
        Query query = queryBuilder.buildFromJSON(json);
        if (queryStorage != null && writeNewQueries) {
            queryStorage.addQuery(query.getQueryId(), json);
        }
        addQuery(query);
    }

    @Override
    public Matcher matchDocument(Document doc) {
        return matchDocument(doc, getMatcher());
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

    @Override
    public void setQueryStorage(QueryStorage queryStorage) {
        this.queryStorage = queryStorage;
    }

    @Override
    public void close() {
        this.queryStorage.close();
    }

    @Override
    public void init() {
        //Disable persistence for new queries so we don't try updating every single query
        writeNewQueries = false;
        if (queryStorage != null) {
            LOGGER.info("initializing schema with stored queries");
            int loadedQueries = 0;
            try {
                for (Map.Entry<Integer, String> entry : queryStorage.getAllQueries()) {
                    addQuery(entry.getValue());
                    loadedQueries++;
                }
            } catch (Exception e) {
                LOGGER.error("problem loading stored queries", e);
            }
            LOGGER.info("done loading " + String.valueOf(loadedQueries) + " queries");
        }
        trim();
        writeNewQueries = true;
    }

    @Override
    public void deleteQuery(Integer queryId) {
        String rawQuery = queryStorage.getRawQuery(queryId);
        if (rawQuery != null) {
            try {
                Query query = queryBuilder.buildFromJSON(rawQuery);
                removePostings(query);
            } catch (MalformedQueryException e) {
                LOGGER.warn("Error parsing query", e);
            }
        }
        queryManager.deleteQuery(queryId);
        queryStorage.deleteQuery(queryId);
    }

    private void removePostings(Query query) {
        Map<Condition, Long> postings = query.getPostings();
        for (Map.Entry<Condition, Long> entry : postings.entrySet()) {
            Condition condition = entry.getKey();
            Long posting = entry.getValue();

            indices.get(condition.getFieldName()).removePosting(condition.getToken(), posting);
        }
        queryManager.addQuery(query);
    }

    @Override
    public void trim() {
        for (FieldIndex field : indices.values()) {
            field.trim();
        }
    }
}
