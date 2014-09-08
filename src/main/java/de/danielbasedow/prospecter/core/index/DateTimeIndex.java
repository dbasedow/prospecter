package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import gnu.trove.list.TLongList;

import java.text.DateFormat;

public class DateTimeIndex extends AbstractFieldIndex {
    private final RangeIndex<Long> index;
    private final DateFormat dateFormat;

    public DateTimeIndex(String name, DateFormat df) {
        super(name);
        index = new RangeIndex<Long>();
        dateFormat = df;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
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
        return FieldType.DATE_TIME;
    }

    @Override
    public void removePosting(Token token, Long posting) {
        index.removePosting(token, posting);
    }
}
