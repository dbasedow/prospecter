package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.*;

/**
 * This class represents an integer field index. Possible indexed conditions can be:
 * field < 1
 * field > 1
 * field = 1
 */
public class IntegerIndex extends AbstractFieldIndex {

    protected RangeIndex<Integer> index;

    public IntegerIndex(String name) {
        super(name);
        index = new RangeIndex<Integer>();
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
        return FieldType.INTEGER;
    }
}
