package de.danielbasedow.prospecter.core;

public class Token<T> {
    T token;
    MatchCondition condition;

    public Token(T token){
        this(token, MatchCondition.NONE);
    }

    public Token(T token, MatchCondition condition) {
        this.token = token;
        this.condition = condition;
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

    public MatchCondition getCondition() {
        return condition;
    }
}
