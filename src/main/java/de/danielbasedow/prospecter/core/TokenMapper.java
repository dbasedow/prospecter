package de.danielbasedow.prospecter.core;

import com.skjegstad.utils.BloomFilter;

public interface TokenMapper {
    public int getTermId(String str, boolean dontGenerateNewIds);

    public void setBloomFilter(BloomFilter<String> bloomFilter);
}
