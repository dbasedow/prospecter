package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;

import java.util.ArrayList;

public interface FieldIndex {
    public String getName();

    public ArrayList<QueryPosting> match();

    public void addPosting(Integer tokenId, QueryPosting posting);
}
