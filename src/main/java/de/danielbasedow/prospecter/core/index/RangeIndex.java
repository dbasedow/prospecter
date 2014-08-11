package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RangeIndex<T> {
    SortedMap<T, List<QueryPosting>> indexEquals;
    SortedMap<T, List<QueryPosting>> indexLessThan;
    SortedMap<T, List<QueryPosting>> indexGreaterThan;

    public RangeIndex() {
        indexEquals = new ConcurrentSkipListMap<T, List<QueryPosting>>();
        indexLessThan = new ConcurrentSkipListMap<T, List<QueryPosting>>();
        indexGreaterThan = new ConcurrentSkipListMap<T, List<QueryPosting>>();
    }

    public List<QueryPosting> match(Field field) {
        List<QueryPosting> postings = new ArrayList<QueryPosting>();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            T tToken = (T) token.getToken();
            collectEqualMatches(postings, tToken);
            collectLessThanMatches(postings, tToken);
            collectGreaterThanMatches(postings, tToken);
        }
        return postings;
    }

    protected void collectEqualMatches(List<QueryPosting> postings, T token) {
        if (indexEquals.containsKey(token)) {
            postings.addAll(indexEquals.get(token));
        }
    }

    protected void collectGreaterThanMatches(List<QueryPosting> postings, T token) {
        /**
         * The indexGreaterThan contains postings for "field > x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are LESS than the field value
         * A field containing y has to return all fields less than y so: -n < 0 < x < y < n
         */
        Map<T, List<QueryPosting>> navigableMap = indexGreaterThan.headMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<T, List<QueryPosting>> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    protected void collectLessThanMatches(List<QueryPosting> postings, T token) {
        /**
         * The indexLessThan contains postings for "field < x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are GREATER than the field value
         * A field containing y has to return all fields greater than y so: -n < 0 < x < y < n
         */
        Map<T, List<QueryPosting>> navigableMap = indexLessThan.tailMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<T, List<QueryPosting>> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    public void addPosting(Token token, QueryPosting posting) {
        T intToken = (T) token.getToken();
        switch (token.getCondition()) {
            case EQUALS:
                getOrCreate(indexEquals, intToken).add(posting);
                break;
            case GREATER_THAN:
                getOrCreate(indexGreaterThan, intToken).add(posting);
                break;
            case GREATER_THAN_EQUALS:
                getOrCreate(indexGreaterThan, intToken).add(posting);
                getOrCreate(indexEquals, intToken).add(posting);
                break;
            case LESS_THAN:
                getOrCreate(indexLessThan, intToken).add(posting);
                break;
            case LESS_THAN_EQUALS:
                getOrCreate(indexLessThan, intToken).add(posting);
                getOrCreate(indexEquals, intToken).add(posting);
                break;
        }
    }

    private List<QueryPosting> getOrCreate(Map<T, List<QueryPosting>> map, T key) {
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
