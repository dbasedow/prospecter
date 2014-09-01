package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Abstract class for FieldIndex implementations.
 */
public abstract class AbstractFieldIndex implements FieldIndex {

    protected String name;

    @SuppressWarnings("WeakerAccess")
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

    @Override
    public void removePosting(Token token, Long posting) {
        throw new NotImplementedException();
    }

}
