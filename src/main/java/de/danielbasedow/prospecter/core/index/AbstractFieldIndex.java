package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;

/**
 * Abstract class for FieldIndex implementations.
 */
public abstract class AbstractFieldIndex implements FieldIndex {

    protected final String name;

    public AbstractFieldIndex(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void trim() {
    }

    public void addPosting(Token token, Long posting) {
        addPosting(token, posting, false);
    }

    public void removePosting(Token token, Long posting) {
        removePosting(token, posting, false);
    }

    @Override
    public void removePosting(Token token, Long posting, boolean not) {
    }

}
