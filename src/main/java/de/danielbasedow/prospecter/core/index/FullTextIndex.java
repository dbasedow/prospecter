package de.danielbasedow.prospecter.core.index;

import com.google.inject.Guice;
import de.danielbasedow.prospecter.core.ProspecterModule;
import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.ArrayList;
import java.util.HashMap;

public class FullTextIndex extends AbstractFieldIndex {
    protected HashMap<Integer, ArrayList<QueryPosting>> index;
    private Analyzer analyzer;

    public FullTextIndex(String name) {
        super(name);
        index = new HashMap<Integer, ArrayList<QueryPosting>>();
        //TODO: analyzer should be configurable
        analyzer = Guice.createInjector(new ProspecterModule()).getInstance(Analyzer.class);
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

    @Override
    public FieldType getFieldType() {
        return FieldType.FULL_TEXT;
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

    public Analyzer getAnalyzer() {
        return analyzer;
    }
}
