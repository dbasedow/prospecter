package de.danielbasedow.prospecter.core.analysis;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.inject.Guice;
import com.google.inject.Injector;
import de.danielbasedow.prospecter.core.TokenMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class LuceneStandardAnalyzer extends LuceneAnalyzer {
    public LuceneStandardAnalyzer(TokenMapper mapper, org.apache.lucene.analysis.Analyzer analyzer) {
        super(mapper);
        luceneAnalyzer = analyzer;
    }

    public static Analyzer make(JsonNode options) {
        Injector injector = Guice.createInjector(new AnalyzerModule());

        CharArraySet stopWordSet = new CharArraySet(Version.LUCENE_4_9, 5, true);
        JsonNode stopWords = options.get("stopwords");
        if (stopWords != null) {
            if (stopWords.getNodeType() == JsonNodeType.ARRAY) {
                for (JsonNode node : stopWords) {
                    if (node != null && node.getNodeType() == JsonNodeType.STRING) {
                        stopWordSet.add(node.asText());
                    }
                }
            } else if (stopWords.getNodeType() == JsonNodeType.STRING && "predefined".equals(stopWords.asText())) {
                stopWordSet = StandardAnalyzer.STOP_WORDS_SET;
            }
        }
        org.apache.lucene.analysis.Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_4_9, stopWordSet);

        TokenMapper mapper = injector.getInstance(TokenMapper.class);
        return new LuceneStandardAnalyzer(mapper, analyzer);
    }
}
