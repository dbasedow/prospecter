package de.danielbasedow.prospecter.core.query.build;


import aima.core.logic.propositional.parsing.ast.Connective;

public class Disjunction extends FlatComplexSentence {
    @Override
    public Connective getConnective() {
        return Connective.OR;
    }

    @Override
    public boolean isOrSentence() {
        return true;
    }
}
