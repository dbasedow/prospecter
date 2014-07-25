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
    public ArrayList<Integer> getTermIds(ArrayList<String> tokens) {
        ArrayList<Integer> termIds = new ArrayList<Integer>();
        for (String token : tokens) {
            termIds.add(getTermId(token));
        }

        return termIds;
    }
}
