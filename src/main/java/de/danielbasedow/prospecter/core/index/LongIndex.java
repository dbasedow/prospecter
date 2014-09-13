package de.danielbasedow.prospecter.core.index;

public class LongIndex extends AbstractRangeFieldIndex<Long> {

    public LongIndex(String name) {
        super(name);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }
}
