package de.danielbasedow.prospecter.core.schema;

public class SchemaNotFoundException extends RuntimeException {
    public SchemaNotFoundException(String msg) {
        super(msg);
    }

    public SchemaNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
