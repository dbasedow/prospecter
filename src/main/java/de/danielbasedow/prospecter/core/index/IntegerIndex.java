package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

/**
 * This class represents an integer field index. Possible indexed conditions can be:
 * field < 1
 * field > 1
 * field = 1
 */
public class IntegerIndex extends AbstractRangeFieldIndex<Integer> {

    public IntegerIndex(String name) {
        super(name);
    }

    @Override
    public TLongList match(Field field) {
        return index.match(field);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.INTEGER;
    }


}
