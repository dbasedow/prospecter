package de.danielbasedow.prospecter.core.query;

public class InvalidQueryException extends RuntimeException {
    public InvalidQueryException(String msg) {
        super(msg);
    }

    public InvalidQueryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
