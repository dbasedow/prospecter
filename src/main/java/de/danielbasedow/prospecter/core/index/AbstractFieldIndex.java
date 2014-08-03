package de.danielbasedow.prospecter.core.index;

public abstract class AbstractFieldIndex implements FieldIndex {

    protected String name;

    public AbstractFieldIndex(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
