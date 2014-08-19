package de.danielbasedow.prospecter.core.index;

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
}
