package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * This class represents an integer field index. Possible indexed conditions can be:
 * field < 1
 * field > 1
 * field = 1
 */
public class IntegerIndex extends AbstractFieldIndex {

    SortedMap<Integer, List<QueryPosting>> indexGreaterThan;
    SortedMap<Integer, List<QueryPosting>> indexLessThan;
    Map<Integer, List<QueryPosting>> indexEqual;

    public IntegerIndex(String name) {
        super(name);
        indexGreaterThan = new ConcurrentSkipListMap<Integer, List<QueryPosting>>();
        indexLessThan = new ConcurrentSkipListMap<Integer, List<QueryPosting>>();
        indexEqual = new ConcurrentHashMap<Integer, List<QueryPosting>>();
    }

    @Override
    public List<QueryPosting> match(Field field) {
        List<QueryPosting> postings = new ArrayList<QueryPosting>();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            Integer intToken = (Integer) token.getToken();
            collectEqualMatches(postings, intToken);
            collectLessThanMatches(postings, intToken);
            collectGreaterThanMatches(postings, intToken);
        }
        return postings;
    }

    protected void collectEqualMatches(List<QueryPosting> postings, Integer token) {
        if (indexEqual.containsKey(token)) {
            postings.addAll(indexEqual.get(token));
        }
    }

    protected void collectGreaterThanMatches(List<QueryPosting> postings, Integer token) {
        /**
         * The indexGreaterThan contains postings for "field > x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are LESS than the field value
         * A field containing y has to return all fields less than y so: -n < 0 < x < y < n
         */
        Map<Integer, List<QueryPosting>> navigableMap = indexGreaterThan.headMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<Integer, List<QueryPosting>> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    protected void collectLessThanMatches(List<QueryPosting> postings, Integer token) {
        /**
         * The indexLessThan contains postings for "field < x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are GREATER than the field value
         * A field containing y has to return all fields greater than y so: -n < 0 < x < y < n
         */
        Map<Integer, List<QueryPosting>> navigableMap = indexLessThan.tailMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<Integer, List<QueryPosting>> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    @Override
    public void addPosting(Token token, QueryPosting posting) {
        Integer intToken = (Integer) token.getToken();
        switch (token.getCondition()) {
            case EQUALS:
                getOrCreate(indexEqual, intToken).add(posting);
                break;
            case GREATER_THAN:
                getOrCreate(indexGreaterThan, intToken).add(posting);
                break;
            case GREATER_THAN_EQUALS:
                getOrCreate(indexGreaterThan, intToken).add(posting);
                getOrCreate(indexEqual, intToken).add(posting);
                break;
            case LESS_THAN:
                getOrCreate(indexLessThan, intToken).add(posting);
                break;
            case LESS_THAN_EQUALS:
                getOrCreate(indexLessThan, intToken).add(posting);
                getOrCreate(indexEqual, intToken).add(posting);
                break;
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.INTEGER;
    }

    private List<QueryPosting> getOrCreate(Map<Integer, List<QueryPosting>> map, Integer key) {
        List<QueryPosting> postings;
        if (map.containsKey(key)) {
            postings = map.get(key);
        } else {
            postings = new ArrayList<QueryPosting>();
            map.put(key, postings);
        }
        return postings;
    }
}
