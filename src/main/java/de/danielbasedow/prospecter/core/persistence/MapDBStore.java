package de.danielbasedow.prospecter.core.persistence;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import org.mapdb.HTreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class MapDBStore implements QueryStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(MapDBStore.class);

    private static final int DEFAULT_UNWRITTEN_CHANGES_LIMIT = 10000;
    private DB database;
    private HTreeMap<Integer, String> map;
    private int unwrittenChangesCount;
    private int unwrittenChangesLimit;
    private final String name;

    public MapDBStore(File file) {
        name = file.getName();
        LOGGER.debug("opening query store " + name);
        database = DBMaker.newFileDB(file).make();
        LOGGER.debug("reading query store " + name);
        map = database.getHashMap("queries");
        unwrittenChangesCount = 0;
        unwrittenChangesLimit = DEFAULT_UNWRITTEN_CHANGES_LIMIT;
    }

    @Override
    public void addQuery(Integer queryId, String json) {
        map.put(queryId, json);
        commitIfNecessary();
    }

    private void commitIfNecessary() {
        unwrittenChangesCount++;
        if (unwrittenChangesCount >= unwrittenChangesLimit) {
            LOGGER.debug("committing changes in query store (auto-commit limit reached)");
            unwrittenChangesCount = 0;
            database.commit();
        }
    }

    public void deleteQuery(Integer queryId) {
        map.remove(queryId);
        commitIfNecessary();
    }

    @Override
    public Set<Map.Entry<Integer, String>> getAllQueries() {
        return map.entrySet();
    }

    @Override
    public void close() {
        LOGGER.debug("closing query store: " + name);
        database.commit();
        database.close();
    }
}
