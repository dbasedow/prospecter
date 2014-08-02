package de.danielbasedow.prospecter.core;

public class Token<T> {
    T token;

    public Token(T token) {
        this.token = token;
    }

    public T getToken() {
        return token;
    }

    public int hashCode() {
        return token.hashCode();
    }

    public boolean equals(Object compare) {
        if (compare instanceof Token) {
            return token.equals(((Token) compare).getToken());
        }
        return false;
    }
}
