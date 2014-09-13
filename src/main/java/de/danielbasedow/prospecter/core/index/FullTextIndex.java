package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;
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

    protected final TIntObjectHashMap<TLongList> index = new TIntObjectHashMap<TLongList>();
    protected final TIntObjectHashMap<TLongList> negativeIndex = new TIntObjectHashMap<TLongList>();

    private final Analyzer analyzer;

    public FullTextIndex(String name, Analyzer analyzer) {
        super(name);
        this.analyzer = analyzer;
    }

    @Override
    public void addPosting(Token token, Long posting, boolean not) {
        TIntObjectHashMap<TLongList> indexToUse = index;
        if (not) {
            indexToUse = negativeIndex;
        }

        TLongList postingList;
        if (indexToUse.containsKey((Integer) token.getToken())) {
            postingList = indexToUse.get((Integer) token.getToken());
        } else {
            postingList = new TLongArrayList();
            indexToUse.put((Integer) token.getToken(), postingList);
        }
        postingList.add(posting);
    }

    @Override
    public void removePosting(Token token, Long posting, boolean not) {
        TIntObjectHashMap<TLongList> indexToUse = index;
        if (not) {
            indexToUse = negativeIndex;
        }

        TLongList postingList = indexToUse.get((Integer) token.getToken());
        if (postingList != null && postingList.contains(posting)) {
            LOGGER.debug("removing posting: " + String.valueOf(posting));
            postingList.remove(posting);
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.FULL_TEXT;
    }

    public TLongList getQueryPostingsForTermId(Token token) {
        Integer t = (Integer) token.getToken();
        if (index.containsKey(t)) {
            return index.get(t);
        }
        return null;
    }

    @Override
    public TLongList match(Field field, boolean negative) {
        TIntObjectHashMap<TLongList> indexToUse = index;
        if (negative) {
            indexToUse = negativeIndex;
        }

        TLongList postings = new TLongArrayList();
        for (Token token : field.getTokens()) {
            Integer t = (Integer) token.getToken();
            TLongList additionalPostings = indexToUse.get(t);
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
