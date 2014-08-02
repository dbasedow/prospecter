package de.danielbasedow.prospecter.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.google.inject.multibindings.Multibinder;
import de.danielbasedow.prospecter.core.analysis.*;
import de.danielbasedow.prospecter.core.analysis.filters.NormalizeWhiteSpaceFilter;
import de.danielbasedow.prospecter.core.analysis.filters.RemoveNonAlphaNumFilter;
import de.danielbasedow.prospecter.core.analysis.filters.ToLowerCaseFilter;

public class ProspecterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QueryManager.class).to(QueryManagerImpl.class).in(Singleton.class);
        bind(TokenMapper.class).to(TokenMapperImpl.class).in(Singleton.class);
        bind(Tokenizer.class).to(TokenizerImpl.class);
        bind(Schema.class).to(SchemaImpl.class).in(Singleton.class);
        bind(Analyzer.class).to(AnalyzerImpl.class);

        Multibinder<Filter> filterBinder = Multibinder.newSetBinder(binder(), Filter.class);
        filterBinder.addBinding().to(NormalizeWhiteSpaceFilter.class);
        filterBinder.addBinding().to(ToLowerCaseFilter.class);
        filterBinder.addBinding().to(RemoveNonAlphaNumFilter.class);

    }
}
