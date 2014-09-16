package de.danielbasedow.prospecter.core.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import com.skjegstad.utils.BloomFilter;
import com.skjegstad.utils.BloomFilterImpl;
import com.skjegstad.utils.FakeBloomFilter;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.TokenMapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Thin wrapper around Lucene's org.apache.lucene.analysis.Analyzer
 */
public abstract class LuceneAnalyzer implements Analyzer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LuceneAnalyzer.class);
    protected final TokenMapper tokenMapper;
    protected org.apache.lucene.analysis.Analyzer luceneAnalyzer;

    @Inject
    public LuceneAnalyzer(TokenMapper mapper) {
        tokenMapper = mapper;
    }

    @Override
    public List<Token> tokenize(String input) throws TokenizerException {
        return tokenize(input, false);
    }

    @Override
    public List<Token> tokenize(String input, boolean dontGenerateNewIds) throws TokenizerException {
        List<Token> tokens = new ArrayList<Token>();
        try {
            TokenStream ts = luceneAnalyzer.tokenStream("_", new StringReader(input));
            CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
            try {
                ts.reset();
                int termId;
                while (ts.incrementToken()) {
                    termId = tokenMapper.getTermId(cta.toString(), dontGenerateNewIds);
                    if (termId != 0) {
                        tokens.add(new Token<Integer>(termId));
                    }
                }
                ts.end();
            } finally {
                ts.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new TokenizerException();
        }
        return tokens;
    }

    public static Analyzer make(JsonNode options) {
        throw new UnsupportedOperationException();
    }

    protected static CharArraySet getStopWords(JsonNode stopWords, CharArraySet defaultStopWords) {
        CharArraySet stopWordSet = new CharArraySet(Version.LUCENE_4_9, 5, true);
        if (stopWords != null) {
            if (stopWords.getNodeType() == JsonNodeType.ARRAY) {
                for (JsonNode node : stopWords) {
                    if (node != null && node.getNodeType() == JsonNodeType.STRING) {
                        stopWordSet.add(node.asText());
                    }
                }
            } else if (stopWords.getNodeType() == JsonNodeType.STRING && "predefined".equals(stopWords.asText())) {
                stopWordSet = defaultStopWords;
            }
        }
        return stopWordSet;
    }

    protected static BloomFilter<String> getBloomFilter(JsonNode options) {
        if (options.get("bloomfilter") == null) {
            LOGGER.info("Using FakeBloomFilter");
            return new FakeBloomFilter<String>();
        }
        ObjectNode bfOptions = (ObjectNode) options.get("bloomfilter");
        double falsePositiveProbability = bfOptions.get("falsePositiveProbability").asDouble(0.01);
        int expectedNumberOfElements = bfOptions.get("expectedNumberOfElements").asInt(1000);
        LOGGER.info("Using BloomFilterImpl with " + String.valueOf(falsePositiveProbability * 100) + "% false probability and " + String.valueOf(expectedNumberOfElements) + " expected number of elements");
        return new BloomFilterImpl<String>(falsePositiveProbability, expectedNumberOfElements);
    }
}
