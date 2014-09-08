package de.danielbasedow.prospecter.core.query.build;

public class PropositionSymbol extends aima.core.logic.propositional.parsing.ast.PropositionSymbol {
    private final Condition condition;

    public PropositionSymbol(String symbol, Condition condition) {
        super(symbol);
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }
}
