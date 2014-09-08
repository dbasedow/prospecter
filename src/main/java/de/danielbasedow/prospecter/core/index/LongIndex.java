package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

public class LongIndex extends AbstractFieldIndex {
    final RangeIndex<Long> index;

    public LongIndex(String name) {
        super(name);
        index = new RangeIndex<Long>();
    }

    @Override
    public TLongList match(Field field) {
        return index.match(field);
    }

    @Override
    public void addPosting(Token token, Long posting) {
        index.addPosting(token, posting);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }

    @Override
    public void removePosting(Token token, Long posting) {
        index.removePosting(token, posting);
    }
}
