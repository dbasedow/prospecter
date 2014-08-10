package de.danielbasedow.prospecter.core.schema;

/**
 * It is possible to build a Schema programmatically. But it is much easier using a SchemaBuilder and use a format
 * like JSON to describe the Schema.
 */
public interface SchemaBuilder {
    public Schema getSchema() throws SchemaConfigurationError;
}
