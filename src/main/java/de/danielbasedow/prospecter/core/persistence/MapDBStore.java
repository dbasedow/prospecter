package de.danielbasedow.prospecter.core.persistence;

import org.mapdb.BTreeMap;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.File;
import java.util.Map;
import java.util.Set;

public class MapDBStore implements QueryStorage {
    private DB database;
    private BTreeMap<Long, String> map;

    public MapDBStore(File file) {
        database = DBMaker.newFileDB(file).make();
        map = database.getTreeMap("queries");
    }

    @Override
    public void addQuery(Long queryId, String json) {
        map.put(queryId, json);
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
