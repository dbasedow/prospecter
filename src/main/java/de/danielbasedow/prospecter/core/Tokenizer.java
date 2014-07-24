package de.danielbasedow.prospecter.core;

import java.util.ArrayList;


public interface Tokenizer {
    Integer getTermId(String str);

    Integer getNewTermId();

    public Integer[] getTermIds(String[] tokens);
}
