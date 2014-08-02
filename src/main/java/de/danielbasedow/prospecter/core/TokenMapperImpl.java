package de.danielbasedow.prospecter.core;

import java.util.ArrayList;
import java.util.HashMap;

public class TokenMapperImpl implements TokenMapper {
    protected int termIdSequence;
    protected HashMap<String, Integer> termMap;

    public TokenMapperImpl() {
        termIdSequence = 0;
        termMap = new HashMap<String, Integer>();
    }

    @Override
    public Integer getTermId(String str, boolean dontGenerateNewIds) {
        if (termMap.containsKey(str)) {
            return termMap.get(str);
        } else if (!dontGenerateNewIds) {
            Integer termId = getNewTermId();
            termMap.put(str, termId);
            return termId;
        }
        return null;
    }

    @Override
    public Integer getNewTermId() {
        return termIdSequence++;
    }

    public ArrayList<Integer> getTermIds(ArrayList<String> tokens) {
        return getTermIds(tokens, false);
    }

    @Override
    public ArrayList<Integer> getTermIds(ArrayList<String> tokens, boolean dontGenerateNewIds) {
        ArrayList<Integer> termIds = new ArrayList<Integer>();
        for (String token : tokens) {
            Integer termId = getTermId(token, dontGenerateNewIds);
            if (termId != null && !termIds.contains(termId)) {
                termIds.add(termId);
            }
        }

        return termIds;
    }
}
