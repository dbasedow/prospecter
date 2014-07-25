package de.danielbasedow.prospecter.core.document;

import java.util.ArrayList;

public class TextField implements Field {
    protected String name;
    protected ArrayList<Integer> termIds;

    public TextField(String name, ArrayList<Integer> termIds) {
        this.name = name;
        this.termIds = termIds;
    }

    @Override
    public ArrayList<Integer> getTermIds() {
        return termIds;
    }

    @Override
    public String getName() {
        return name;
    }
}
