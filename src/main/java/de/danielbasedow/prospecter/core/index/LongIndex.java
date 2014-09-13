package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

public class LongIndex extends AbstractRangeFieldIndex<Long> {
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
    public FieldType getFieldType() {
        return FieldType.LONG;
    }
}
