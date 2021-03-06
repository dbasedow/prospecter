package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.Matcher;
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

    private final Analyzer analyzer;

    public FullTextIndex(String name, Analyzer analyzer) {
        super(name);
        this.analyzer = analyzer;
    }

    @Override
    public void addPosting(Token token, Long posting) {
        TLongList postingList;
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
        TLongList postingList = index.get((Integer) token.getToken());
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
    public void match(Field field, Matcher matcher) {
        for (Token token : field.getTokens()) {
            Integer t = (Integer) token.getToken();
            TLongList additionalPostings = index.get(t);
            if (additionalPostings != null) {
                matcher.addHits(additionalPostings);
            }
        }
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
