package de.danielbasedow.prospecter.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.analysis.AnalyzerImpl;
import de.danielbasedow.prospecter.core.analysis.Tokenizer;
import de.danielbasedow.prospecter.core.analysis.TokenizerImpl;

public class ProspecterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QueryManager.class).to(QueryManagerImpl.class).in(Singleton.class);
        bind(TokenMapper.class).to(TokenMapperImpl.class).in(Singleton.class);
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(Schema.class).to(SchemaImpl.class).in(Singleton.class);
        bind(Analyzer.class).to(AnalyzerImpl.class);
    }
}
