package de.danielbasedow.prospecter.core;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ProspecterModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(QueryManager.class).to(QueryManagerImpl.class).in(Singleton.class);
        bind(Tokenizer.class).to(TokenizerImpl.class).in(Singleton.class);
        bind(Schema.class).to(SchemaImpl.class).in(Singleton.class);
    }
}
