package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.HashMap;

public class FullTextIndex extends AbstractFieldIndex {
    protected HashMap<Integer, ArrayList<QueryPosting>> index;

    public FullTextIndex(String name) {
        super(name);
        index = new HashMap<Integer, ArrayList<QueryPosting>>();
    }

    public void addPosting(Token token, QueryPosting posting) {
        ArrayList<QueryPosting> postingList;
        if (index.containsKey((Integer) token.getToken())) {
            postingList = index.get((Integer) token.getToken());
        } else {
            postingList = new ArrayList<QueryPosting>();
            index.put((Integer) token.getToken(), postingList);
        }
        postingList.add(posting);
    }

    public ArrayList<QueryPosting> getQueryPostingsForTermId(Token token) {
        Integer t = (Integer) token.getToken();
        if (index.containsKey(t)) {
            return index.get(t);
        }
        return null;
    }

    @Override
    public ArrayList<QueryPosting> match(Field field) {
        ArrayList<QueryPosting> postings = new ArrayList<QueryPosting>();
        for (Token token : field.getTokens()) {
            Integer t = (Integer) token.getToken();
            ArrayList<QueryPosting> additionalPostings = index.get(t);
            if (additionalPostings != null) {
                postings.addAll(additionalPostings);
            }
        }
        return postings;
    }

}
