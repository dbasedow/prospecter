package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;

import java.util.List;

public interface FieldIndex {
    public String getName();

    public List<QueryPosting> match(Field field);

    public void addPosting(Token token, QueryPosting posting);

    public FieldType getFieldType();
}
