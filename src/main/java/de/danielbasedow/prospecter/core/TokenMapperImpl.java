package de.danielbasedow.prospecter.core;

import com.skjegstad.utils.BloomFilter;
import com.skjegstad.utils.BloomFilterImpl;
import com.skjegstad.utils.FakeBloomFilter;
import gnu.trove.map.hash.TObjectIntHashMap;

public class TokenMapperImpl implements TokenMapper {
    protected int termIdSequence;
    protected final TObjectIntHashMap<String> termMap = new TObjectIntHashMap<String>();
    protected BloomFilter<String> bloomFilter;

    public TokenMapperImpl() {
        termIdSequence = 1;
        bloomFilter = new FakeBloomFilter<String>();
    }

    @Override
    public int getTermId(String str, boolean dontGenerateNewIds) {
        int termId = 0;
        if (bloomFilter.contains(str)) {
            termId = termMap.get(str);
        }
        if (termId == 0 && !dontGenerateNewIds) {
            synchronized (this) {
                termId = getNewTermId();
                termMap.put(str, termId);
                bloomFilter.add(str);
            }
        }
        return termId;
    }

    @Override
    public void setBloomFilter(BloomFilter<String> bloomFilter) {
        this.bloomFilter = bloomFilter;
    }

    public int getNewTermId() {
        return termIdSequence++;
    }
}
