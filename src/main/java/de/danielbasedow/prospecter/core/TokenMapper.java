package de.danielbasedow.prospecter.core;

import java.util.ArrayList;


public interface TokenMapper {
    Integer getTermId(String str);

    Integer getNewTermId();

    public ArrayList<Integer> getTermIds(ArrayList<String> tokens);
}
