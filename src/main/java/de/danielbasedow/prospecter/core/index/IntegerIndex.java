package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * This class represents an integer field index. Possible indexed conditions can be:
 * field < 1
 * field > 1
 * field = 1
 */
public class IntegerIndex extends AbstractFieldIndex {
    /**
     * indexGreaterThan contains postings for field > x. During matching all entries smaller than
     * the fields actual value will be retrieved.
     */
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
        }
        return postings;
    }

    protected void collectEqualMatches(ArrayList<QueryPosting> postings, Integer token) {
        if (indexEqual.containsKey(token)) {
            postings.addAll(indexEqual.get(token));
        }
    }

    protected void collectGreaterMatches(ArrayList<QueryPosting> postings, Integer token) {
        //TODO: implement
    }

    protected void collectLessMatches(ArrayList<QueryPosting> postings, Integer token) {
        //TODO: implement
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
