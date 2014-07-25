package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;

public interface FieldIndex {
    public String getName();

    public QueryPosting[] match();

    public void addPosting(Integer tokenId, QueryPosting posting);
}
