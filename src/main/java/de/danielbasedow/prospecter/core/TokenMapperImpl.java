package de.danielbasedow.prospecter.core;

import gnu.trove.map.hash.TObjectIntHashMap;

import java.util.ArrayList;
import java.util.List;

public class TokenMapperImpl implements TokenMapper {
    protected int termIdSequence;
    protected final TObjectIntHashMap<String> termMap = new TObjectIntHashMap<String>();

    public TokenMapperImpl() {
        termIdSequence = 1;
    }

    @Override
    public int getTermId(String str, boolean dontGenerateNewIds) {
        int termId = termMap.get(str);
        if (termId == 0 && !dontGenerateNewIds) {
            synchronized (this) {
                termId = getNewTermId();
                termMap.put(str, termId);
            }
        }
        return termId;
    }

    public int getNewTermId() {
        return termIdSequence++;
    }
}
