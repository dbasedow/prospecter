package de.danielbasedow.prospecter.core.query.build;

import aima.core.logic.propositional.parsing.ast.AtomicSentence;
import de.danielbasedow.prospecter.core.query.Condition;

public class PropositionSymbol extends AtomicSentence implements aima.core.logic.propositional.parsing.ast.PropositionSymbol {
    public static final String TRUE_SYMBOL = "True";
    public static final String FALSE_SYMBOL = "False";
    public static final AbstractPropositionalSymbol TRUE = new AbstractPropositionalSymbol(TRUE_SYMBOL);
    public static final AbstractPropositionalSymbol FALSE = new AbstractPropositionalSymbol(FALSE_SYMBOL);

    private final Condition condition;

    public PropositionSymbol(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    @Override
    public boolean isAlwaysTrue() {
        return false;
    }

    @Override
    public boolean isAlwaysFalse() {
        return false;
    }

    @Override
    public String getSymbol() {
        return condition.getSymbolName();
    }

}
