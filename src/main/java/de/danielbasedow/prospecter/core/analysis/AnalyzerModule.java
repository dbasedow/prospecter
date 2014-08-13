package de.danielbasedow.prospecter.core.analysis;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import de.danielbasedow.prospecter.core.TokenMapper;
import de.danielbasedow.prospecter.core.TokenMapperImpl;


public class AnalyzerModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TokenMapper.class).to(TokenMapperImpl.class).in(Singleton.class);
    }
}
