package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Index enabling full text search
 */
public class FullTextIndex extends AbstractFieldIndex {
    protected HashMap<Integer, List<QueryPosting>> index;
    private Analyzer analyzer;

    public FullTextIndex(String name, Analyzer analyzer) {
        super(name);
        index = new HashMap<Integer, List<QueryPosting>>();
        this.analyzer = analyzer;
    }

    public void addPosting(Token token, QueryPosting posting) {
        List<QueryPosting> postingList;
        if (index.containsKey((Integer) token.getToken())) {
            postingList = index.get((Integer) token.getToken());
        } else {
            postingList = new ArrayList<QueryPosting>();
            index.put((Integer) token.getToken(), postingList);
        }
        postingList.add(posting);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.FULL_TEXT;
    }

    public List<QueryPosting> getQueryPostingsForTermId(Token token) {
        Integer t = (Integer) token.getToken();
        if (index.containsKey(t)) {
            return index.get(t);
        }
        return null;
    }

    @Override
    public List<QueryPosting> match(Field field) {
        List<QueryPosting> postings = new ArrayList<QueryPosting>();
        for (Token token : field.getTokens()) {
            Integer t = (Integer) token.getToken();
            List<QueryPosting> additionalPostings = index.get(t);
            if (additionalPostings != null) {
                postings.addAll(additionalPostings);
            }
        }
        return postings;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
