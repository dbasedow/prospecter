package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.array.TLongArrayList;

public class DoubleIndex extends AbstractFieldIndex {
    RangeIndex<Double> index;

    public DoubleIndex(String name) {
        super(name);
        index = new RangeIndex<Double>();
    }

    @Override
    public TLongArrayList match(Field field) {
        return index.match(field);
    }

    @Override
    public void addPosting(Token token, QueryPosting posting) {
        index.addPosting(token, posting);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }
}
