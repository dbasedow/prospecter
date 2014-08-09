package de.danielbasedow.prospecter.core.analysis;

import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.TokenMapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class LuceneAnalyzer implements Analyzer {
    private TokenMapper tokenMapper;
    private org.apache.lucene.analysis.Analyzer luceneAnalyzer;

    @Inject
    public LuceneAnalyzer(TokenMapper mapper) {
        tokenMapper = mapper;
        luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_4_9, CharArraySet.EMPTY_SET);
    }

    @Override
    public List<Token> tokenize(String input) throws TokenizerException {
        return tokenize(input, false);
    }

    @Override
    public List<Token> tokenize(String input, boolean dontGenerateNewIds) throws TokenizerException {
        List<String> tokens = new ArrayList<String>();
        try {
            TokenStream ts = luceneAnalyzer.tokenStream("_", new StringReader(input));
            CharTermAttribute cta = ts.addAttribute(CharTermAttribute.class);
            try {
                ts.reset();
                while (ts.incrementToken()) {
                    tokens.add(cta.toString());
                }
                ts.end();
            } finally {
                ts.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new TokenizerException();
        }
        return tokenMapper.getTermIds(tokens, dontGenerateNewIds);
    }
}
