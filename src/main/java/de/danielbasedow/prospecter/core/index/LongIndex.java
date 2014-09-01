package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.query.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.array.TLongArrayList;

public class LongIndex extends AbstractFieldIndex {
    RangeIndex<Long> index;

    public LongIndex(String name) {
        super(name);
        index = new RangeIndex<Long>();
    }

    @Override
    public TLongArrayList match(Field field) {
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
