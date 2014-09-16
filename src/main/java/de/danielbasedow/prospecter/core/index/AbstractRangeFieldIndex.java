package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

public abstract class AbstractRangeFieldIndex<T> extends AbstractFieldIndex {
    protected final RangeIndex<T> index = new RangeIndex<T>();

    public AbstractRangeFieldIndex(String name) {
        super(name);
    }

    @Override
    public TLongList match(Field field, Matcher matcher) {
        return index.match(field);
    }

    @Override
    public void addPosting(Token token, Long posting, boolean not) {
        index.addPosting(token, posting);
    }

    @Override
    public void removePosting(Token token, Long posting, boolean not) {
        index.removePosting(token, posting);
    }
}
