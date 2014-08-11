package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringIndex extends AbstractFieldIndex {
    protected Map<String, List<QueryPosting>> index;

    public StringIndex(String name) {
        super(name);
        index = new ConcurrentHashMap<String, List<QueryPosting>>();
    }

    @Override
    public List<QueryPosting> match(Field field) {
        List<QueryPosting> postings = new ArrayList<QueryPosting>();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            String strToken = (String) token.getToken();
            if (index.containsKey(strToken)) {
                postings.addAll(index.get(strToken));
            }
        }
        return postings;
    }

    @Override
    public void addPosting(Token token, QueryPosting posting) {
        String tokenStr = (String) token.getToken();
        addOrCreate(tokenStr, posting);
    }

    public void addOrCreate(String token, QueryPosting posting) {
        if (index.containsKey(token)) {
            index.get(token).add(posting);
        } else {
            List<QueryPosting> postings = new ArrayList<QueryPosting>();
            postings.add(posting);
            index.put(token, postings);
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.STRING;
    }
}
