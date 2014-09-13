package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

public class DoubleIndex extends AbstractRangeFieldIndex<Double> {

    public DoubleIndex(String name) {
        super(name);
    }

    @Override
    public TLongList match(Field field) {
        return index.match(field);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }
}
