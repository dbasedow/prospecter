package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

public class DoubleIndex extends AbstractFieldIndex {
    final RangeIndex<Double> index;

    public DoubleIndex(String name) {
        super(name);
        index = new RangeIndex<Double>();
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
