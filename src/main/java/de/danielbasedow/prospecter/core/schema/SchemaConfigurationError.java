package de.danielbasedow.prospecter.core.schema;

public class SchemaConfigurationError extends Exception {
    public SchemaConfigurationError(String msg) {
        super(msg);
    }

    public SchemaConfigurationError(String msg, Throwable cause) {
        super(msg, cause);
    }
}
