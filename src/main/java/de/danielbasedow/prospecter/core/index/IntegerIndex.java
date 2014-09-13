package de.danielbasedow.prospecter.core.index;

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
    public FieldType getFieldType() {
        return FieldType.INTEGER;
    }


}
