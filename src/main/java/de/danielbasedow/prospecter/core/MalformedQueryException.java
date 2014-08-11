package de.danielbasedow.prospecter.core;


public class MalformedQueryException extends Exception {
    public MalformedQueryException(String msg) {
        super(msg);
    }

    public MalformedQueryException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
