package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringIndex extends AbstractFieldIndex {
    protected final Map<String, TLongList> index;

    public StringIndex(String name) {
        super(name);
        index = new ConcurrentHashMap<String, TLongList>();
    }

    @Override
    public TLongList match(Field field) {
        TLongList postings = new TLongArrayList();
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
    public void addPosting(Token token, Long posting, boolean not) {
        String tokenStr = (String) token.getToken();
        addOrCreate(tokenStr, posting);
    }

    @Override
    public void removePosting(Token token, Long posting, boolean not) {
        String tokenStr = (String) token.getToken();

        TLongList postingList = getOrCreatePostingList(tokenStr);
        postingList.remove(posting);
    }

    public void addOrCreate(String token, Long posting) {
        TLongList postingList = getOrCreatePostingList(token);
        postingList.add(posting);
    }

    public TLongList getOrCreatePostingList(String token) {
        TLongList postingList = index.get(token);
        if (postingList == null) {
            postingList = new TLongArrayList();
            index.put(token, postingList);
        }
        return postingList;
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.STRING;
    }
}
