package de.danielbasedow.prospecter.core.query.build;

import de.danielbasedow.prospecter.core.query.Condition;

public class PropositionSymbol extends AbstractPropositionalSymbol {
    private final Condition condition;

    public PropositionSymbol(String symbol, Condition condition) {
        super(symbol);
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }
}
