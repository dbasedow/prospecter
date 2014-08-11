package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.List;

public class LongIndex extends AbstractFieldIndex {
    RangeIndex<Long> index;

    public LongIndex(String name) {
        super(name);
        index = new RangeIndex<Long>();
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
        return FieldType.LONG;
    }
}
