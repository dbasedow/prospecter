package de.danielbasedow.prospecter.core;

import java.util.ArrayList;
import java.util.HashMap;

public class TokenizerImpl implements Tokenizer {
    protected int termIdSequence;
    protected HashMap<String, Integer> termMap;

    public TokenizerImpl() {
        termIdSequence = 0;
        termMap = new HashMap<String, Integer>();
    }

    @Override
    public Integer getTermId(String str) {
        if (termMap.containsKey(str)) {
            return termMap.get(str);
        } else {
            Integer termId = getNewTermId();
            termMap.put(str, termId);
            return termId;
        }
    }

    @Override
    public Integer getNewTermId() {
        return termIdSequence++;
    }

    @Override
    public Integer[] getTermIds(String[] tokens) {
        Integer[] termIds = new Integer[tokens.length];
        int i = 0;
        for (String token : tokens) {
            termIds[i] = getTermId(token);
            i++;
        }

        return termIds;
    }
}
