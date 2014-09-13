package de.danielbasedow.prospecter.core.index;

public class DoubleIndex extends AbstractRangeFieldIndex<Double> {

    public DoubleIndex(String name) {
        super(name);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.LONG;
    }
}
