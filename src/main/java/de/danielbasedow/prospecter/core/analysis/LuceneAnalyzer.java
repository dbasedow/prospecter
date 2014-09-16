package de.danielbasedow.prospecter.core.analysis;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.TokenMapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Thin wrapper around Lucene's org.apache.lucene.analysis.Analyzer
 */
public abstract class LuceneAnalyzer implements Analyzer {
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
                while (ts.incrementToken()) {
                    int termId = tokenMapper.getTermId(cta.toString(), dontGenerateNewIds);
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
}
