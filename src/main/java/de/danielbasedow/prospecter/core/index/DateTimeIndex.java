package de.danielbasedow.prospecter.core.index;

import java.text.DateFormat;

public class DateTimeIndex extends AbstractRangeFieldIndex<Long> {
    private final DateFormat dateFormat;

    public DateTimeIndex(String name, DateFormat df) {
        super(name);
        dateFormat = df;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.DATE_TIME;
    }

}
