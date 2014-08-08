package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    ConcurrentSkipListMap<Integer, ArrayList<QueryPosting>> indexGreaterThan;
    ConcurrentSkipListMap<Integer, ArrayList<QueryPosting>> indexLessThan;
    ConcurrentHashMap<Integer, ArrayList<QueryPosting>> indexEqual;

    public IntegerIndex(String name) {
        super(name);
        indexGreaterThan = new ConcurrentSkipListMap<Integer, ArrayList<QueryPosting>>();
        indexLessThan = new ConcurrentSkipListMap<Integer, ArrayList<QueryPosting>>();
        indexEqual = new ConcurrentHashMap<Integer, ArrayList<QueryPosting>>();
    }

    @Override
    public ArrayList<QueryPosting> match(Field field) {
        ArrayList<QueryPosting> postings = new ArrayList<QueryPosting>();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            Integer intToken = (Integer) token.getToken();
            collectEqualMatches(postings, intToken);
            collectLessThanMatches(postings, intToken);
            collectGreaterThanMatches(postings, intToken);
        }
        return postings;
    }

    protected void collectEqualMatches(ArrayList<QueryPosting> postings, Integer token) {
        if (indexEqual.containsKey(token)) {
            postings.addAll(indexEqual.get(token));
        }
    }

    protected void collectGreaterThanMatches(ArrayList<QueryPosting> postings, Integer token) {
        /**
         * The indexGreaterThan contains postings for "field > x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are LESS than the field value
         * A field containing y has to return all fields less than y so: -n < 0 < x < y < n
         */
        ConcurrentNavigableMap<Integer, ArrayList<QueryPosting>> navigableMap = indexGreaterThan.headMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<Integer, ArrayList<QueryPosting>> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    protected void collectLessThanMatches(ArrayList<QueryPosting> postings, Integer token) {
        /**
         * The indexLessThan contains postings for "field < x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are GREATER than the field value
         * A field containing y has to return all fields greater than y so: -n < 0 < x < y < n
         */
        ConcurrentNavigableMap<Integer, ArrayList<QueryPosting>> navigableMap = indexLessThan.tailMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<Integer, ArrayList<QueryPosting>> entry : navigableMap.entrySet()) {
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

    private ArrayList<QueryPosting> getOrCreate(Map<Integer, ArrayList<QueryPosting>> map, Integer key) {
        ArrayList<QueryPosting> postings;
        if (map.containsKey(key)) {
            postings = map.get(key);
        } else {
            postings = new ArrayList<QueryPosting>();
            map.put(key, postings);
        }
        return postings;
    }
}
