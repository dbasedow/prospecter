package de.danielbasedow.prospecter.core.document;

public class MalformedDocumentException extends RuntimeException {
    public MalformedDocumentException(String msg) {
        super(msg);
    }

    public MalformedDocumentException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
