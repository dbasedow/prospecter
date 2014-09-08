package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class RangeIndex<T> {

    protected final SortedMap<T, TLongList> indexEquals;
    protected final SortedMap<T, TLongList> indexLessThan;
    protected final SortedMap<T, TLongList> indexGreaterThan;

    public RangeIndex() {
        indexEquals = new ConcurrentSkipListMap<T, TLongList>();
        indexLessThan = new ConcurrentSkipListMap<T, TLongList>();
        indexGreaterThan = new ConcurrentSkipListMap<T, TLongList>();
    }

    public TLongList match(Field field) {
        TLongList postings = new TLongArrayList();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            T tToken = (T) token.getToken();
            collectEqualMatches(postings, tToken);
            collectLessThanMatches(postings, tToken);
            collectGreaterThanMatches(postings, tToken);
        }
        return postings;
    }

    protected void collectEqualMatches(TLongList postings, T token) {
        if (indexEquals.containsKey(token)) {
            postings.addAll(indexEquals.get(token));
        }
    }

    protected void collectGreaterThanMatches(TLongList postings, T token) {
        /**
         * The indexGreaterThan contains postings for "field > x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are LESS than the field value
         * A field containing y has to return all fields less than y so: -n < 0 < x < y < n
         */
        Map<T, TLongList> navigableMap = indexGreaterThan.headMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<T, TLongList> entry : navigableMap.entrySet()) {
                postings.addAll(entry.getValue());
            }
        }
    }

    protected void collectLessThanMatches(TLongList postings, T token) {
        /**
         * The indexLessThan contains postings for "field < x" so if x is 10 a posting would be added for 10
         * in order to get the relevant postings we have to look at all key that are GREATER than the field value
         * A field containing y has to return all fields greater than y so: -n < 0 < x < y < n
         */
        Map<T, TLongList> navigableMap = indexLessThan.tailMap(token);
        if (navigableMap.size() > 0) {
            for (Map.Entry<T, TLongList> entry : navigableMap.entrySet()) {
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

    private TLongList getOrCreate(Map<T, TLongList> map, T key) {
        TLongList postings = map.get(key);
        if (postings == null) {
            postings = new TLongArrayList();
            map.put(key, postings);
        }
        return postings;
    }
}
