package de.danielbasedow.prospecter.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import de.danielbasedow.prospecter.core.analysis.*;
import de.danielbasedow.prospecter.core.schema.Schema;
import de.danielbasedow.prospecter.core.schema.SchemaImpl;

public class ProspecterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TokenMapper.class).to(TokenMapperImpl.class).in(Singleton.class);
        bind(Schema.class).to(SchemaImpl.class).in(Singleton.class);
        //bind(Analyzer.class).to(AnalyzerImpl.class);
        bind(Analyzer.class).to(LuceneAnalyzer.class);

    }
}
