package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.array.TLongArrayList;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RangeIndex<T> {
    SortedMap<T, TLongArrayList> indexEquals;
    SortedMap<T, TLongArrayList> indexLessThan;
    SortedMap<T, TLongArrayList> indexGreaterThan;

    public RangeIndex() {
        indexEquals = new ConcurrentSkipListMap<T, TLongArrayList>();
        indexLessThan = new ConcurrentSkipListMap<T, TLongArrayList>();
        indexGreaterThan = new ConcurrentSkipListMap<T, TLongArrayList>();
    }

    public TLongArrayList match(Field field) {
        TLongArrayList postings = new TLongArrayList();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            T tToken = (T) token.getToken();
            collectEqualMatches(postings, tToken);
            collectLessThanMatches(postings, tToken);
            collectGreaterThanMatches(postings, tToken);
        }
        return postings;
    }

    protected void collectEqualMatches(TLongArrayList postings, T token) {
        if (indexEquals.containsKey(token)) {
            postings.addAll(indexEquals.get(token));
        }
    }

    protected void collectGreaterThanMatches(TLongArrayList postings, T token) {
        /**
         * The indexGreaterThan contains postings for "field > x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are LESS than the field value
         * A field containing y has to return all fields less than y so: -n < 0 < x < y < n
         */
        Map<T, TLongArrayList> navigableMap = indexGreaterThan.headMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<T, TLongArrayList> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    protected void collectLessThanMatches(TLongArrayList postings, T token) {
        /**
         * The indexLessThan contains postings for "field < x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are GREATER than the field value
         * A field containing y has to return all fields greater than y so: -n < 0 < x < y < n
         */
        Map<T, TLongArrayList> navigableMap = indexLessThan.tailMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<T, TLongArrayList> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    public void addPosting(Token token, Long posting) {
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

    public void removePosting(Token token, Long posting) {
        T intToken = (T) token.getToken();
        switch (token.getCondition()) {
            case EQUALS:
                getOrCreate(indexEquals, intToken).remove(posting);
                break;
            case GREATER_THAN:
                getOrCreate(indexGreaterThan, intToken).remove(posting);
                break;
            case GREATER_THAN_EQUALS:
                getOrCreate(indexGreaterThan, intToken).remove(posting);
                getOrCreate(indexEquals, intToken).remove(posting);
                break;
            case LESS_THAN:
                getOrCreate(indexLessThan, intToken).remove(posting);
                break;
            case LESS_THAN_EQUALS:
                getOrCreate(indexLessThan, intToken).remove(posting);
                getOrCreate(indexEquals, intToken).remove(posting);
                break;
        }
    }

    private TLongArrayList getOrCreate(Map<T, TLongArrayList> map, T key) {
        TLongArrayList postings;
        if (map.containsKey(key)) {
            postings = map.get(key);
        } else {
            postings = new TLongArrayList();
            map.put(key, postings);
        }
        return postings;
    }
}
