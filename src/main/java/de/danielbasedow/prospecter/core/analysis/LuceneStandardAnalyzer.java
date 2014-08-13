package de.danielbasedow.prospecter.core.analysis;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import de.danielbasedow.prospecter.core.TokenMapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

public class LuceneStandardAnalyzer extends LuceneAnalyzer {
    @Inject
    public LuceneStandardAnalyzer(TokenMapper mapper) {
        super(mapper);
        luceneAnalyzer = new StandardAnalyzer(Version.LUCENE_4_9, CharArraySet.EMPTY_SET);
    }

    public static Analyzer make(JsonNode options) {
        Injector injector = Guice.createInjector(new AnalyzerModule());
        Analyzer analyzer = injector.getInstance(LuceneStandardAnalyzer.class);
        return analyzer;
    }
}
