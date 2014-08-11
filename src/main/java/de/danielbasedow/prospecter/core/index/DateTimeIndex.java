package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.text.DateFormat;
import java.util.List;

public class DateTimeIndex extends AbstractFieldIndex {
    private RangeIndex<Long> index;
    private DateFormat dateFormat;

    public DateTimeIndex(String name, DateFormat df) {
        super(name);
        index = new RangeIndex<Long>();
        dateFormat = df;
    }

    public DateFormat getDateFormat() {
        return dateFormat;
    }

    @Override
    public List<QueryPosting> match(Field field) {
        return index.match(field);
    }

    @Override
    public void addPosting(Token token, QueryPosting posting) {
        index.addPosting(token, posting);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.DATE_TIME;
    }
}
