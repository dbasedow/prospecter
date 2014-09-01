package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.query.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Index enabling full text search
 */
public class FullTextIndex extends AbstractFieldIndex {
    private static final Logger LOGGER = LoggerFactory.getLogger(FullTextIndex.class);

    protected TIntObjectHashMap<TLongArrayList> index;
    private Analyzer analyzer;

    public FullTextIndex(String name, Analyzer analyzer) {
        super(name);
        index = new TIntObjectHashMap<TLongArrayList>();
        this.analyzer = analyzer;
    }

    public void addPosting(Token token, Long posting) {
        TLongArrayList postingList;
        if (index.containsKey((Integer) token.getToken())) {
            postingList = index.get((Integer) token.getToken());
        } else {
            postingList = new TLongArrayList();
            index.put((Integer) token.getToken(), postingList);
        }
        postingList.add(posting);
    }

    @Override
    public void removePosting(Token token, Long posting) {
        TLongArrayList postingList = index.get((Integer) token.getToken());
        if (postingList != null && postingList.contains(posting)) {
            LOGGER.debug("removing posting: " + String.valueOf(posting));
            postingList.remove(posting);
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.FULL_TEXT;
    }

    public TLongArrayList getQueryPostingsForTermId(Token token) {
        Integer t = (Integer) token.getToken();
        if (index.containsKey(t)) {
            return index.get(t);
        }
        return null;
    }

    @Override
    public TLongArrayList match(Field field) {
        TLongArrayList postings = new TLongArrayList();
        for (Token token : field.getTokens()) {
            Integer t = (Integer) token.getToken();
            TLongArrayList additionalPostings = index.get(t);
            if (additionalPostings != null) {
                postings.addAll(additionalPostings);
            }
        }
        return postings;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void trim() {
        for (Object list : index.values()) {
            if (list instanceof ArrayList) {
                ((ArrayList) list).trimToSize();
            }
        }
    }
}
