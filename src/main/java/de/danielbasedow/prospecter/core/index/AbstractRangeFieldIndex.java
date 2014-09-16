package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

public abstract class AbstractRangeFieldIndex<T> extends AbstractFieldIndex {
    protected final RangeIndex<T> index = new RangeIndex<T>();
    protected final RangeIndex<T> negativeIndex = new RangeIndex<T>();

    public AbstractRangeFieldIndex(String name) {
        super(name);
    }

    @Override
    public TLongList match(Field field, Matcher matcher, boolean negative) {
        if (negative) {
            return negativeIndex.match(field);
        }
        return index.match(field);
    }

    @Override
    public void addPosting(Token token, Long posting, boolean not) {
        if (not) {
            negativeIndex.addPosting(token, posting);
        } else {
            index.addPosting(token, posting);
        }
    }

    @Override
    public void removePosting(Token token, Long posting, boolean not) {
        if (not) {
            negativeIndex.removePosting(token, posting);
        } else {
            index.removePosting(token, posting);
        }
    }
}
