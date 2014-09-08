package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TLongArrayList;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StringIndex extends AbstractFieldIndex {
    protected final Map<String, TLongArrayList> index;

    public StringIndex(String name) {
        super(name);
        index = new ConcurrentHashMap<String, TLongArrayList>();
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
    public void addPosting(Token token, Long posting) {
        String tokenStr = (String) token.getToken();
        addOrCreate(tokenStr, posting);
    }

    @Override
    public void removePosting(Token token, Long posting) {
        String tokenStr = (String) token.getToken();

        TLongArrayList postingList = getOrCreatePostingList(tokenStr);
        synchronized (postingList) {
            postingList.remove(posting);
        }
    }

    public void addOrCreate(String token, Long posting) {
        TLongArrayList postingList = getOrCreatePostingList(token);
        synchronized (postingList) {
            postingList.add(posting);
        }
    }

    public synchronized TLongArrayList getOrCreatePostingList(String token) {
        TLongArrayList postingList = index.get(token);
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
