package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.HashMap;

public class FullTextIndex implements FieldIndex {
    protected HashMap<Token, ArrayList<QueryPosting>> index;
    protected String name;

    public FullTextIndex(String name) {
        this.name = name;
        index = new HashMap<Token, ArrayList<QueryPosting>>();
    }

    public void addPosting(Token token, QueryPosting posting) {
        ArrayList<QueryPosting> postingList;
        if (index.containsKey(token)) {
            postingList = index.get(token);
        } else {
            postingList = new ArrayList<QueryPosting>();
            index.put(token, postingList);
        }
        postingList.add(posting);
    }

    public ArrayList<QueryPosting> getQueryPostingsForTermId(Token token) {
        if (index.containsKey(token)) {
            return index.get(token);
        }
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<QueryPosting> match(Field field) {
        ArrayList<QueryPosting> postings = new ArrayList<QueryPosting>();
        for (Token token : field.getTokens()) {
            ArrayList<QueryPosting> additionalPostings = index.get(token);
            if (additionalPostings != null) {
                postings.addAll(additionalPostings);
            }
        }
        return postings;
    }

}
