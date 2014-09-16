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
    public void match(Field field, Matcher matcher) {
        index.match(field, matcher);
    }

    @Override
    public void addPosting(Token token, Long posting) {
        index.addPosting(token, posting);
    }

    @Override
    public void removePosting(Token token, Long posting) {
        index.removePosting(token, posting);
    }
}
