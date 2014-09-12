package de.danielbasedow.prospecter.core.query.build;

import aima.core.logic.propositional.parsing.ast.Connective;

public class Conjunction extends FlatComplexSentence {

    @Override
    public Connective getConnective() {
        return Connective.AND;
    }

    public boolean isAndSentence() {
        return true;
    }

}
