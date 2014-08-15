package de.danielbasedow.prospecter.core.persistence;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class MapDBStore implements QueryStorage {
    private static final int DEFAULT_UNWRITTEN_CHANGES_LIMIT = 10000;
    private DB database;
    private HTreeMap<Long, String> map;
    private int unwrittenChangesCount;
    private int unwrittenChangesLimit;

    public MapDBStore(File file) {
        database = DBMaker.newFileDB(file).make();
        map = database.getHashMap("queries");
        unwrittenChangesCount = 0;
        unwrittenChangesLimit = DEFAULT_UNWRITTEN_CHANGES_LIMIT;
    }

    @Override
    public void addQuery(Long queryId, String json) {
        map.put(queryId, json);
        commitIfNecessary();
    }

    private void commitIfNecessary() {
        unwrittenChangesCount++;
        if (unwrittenChangesCount >= unwrittenChangesLimit) {
            unwrittenChangesCount = 0;
            database.commit();
        }
    }

    public void deleteQuery(Long queryId) {
        map.remove(queryId);
        commitIfNecessary();
    }

    @Override
    public Set<Map.Entry<Long, String>> getAllQueries() {
        return map.entrySet();
    }

    @Override
    public void close() {
        database.commit();
        database.close();
    }
}
