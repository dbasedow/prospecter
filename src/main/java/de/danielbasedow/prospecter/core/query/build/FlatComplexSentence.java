package de.danielbasedow.prospecter.core.query.build;

import aima.core.logic.propositional.parsing.PLVisitor;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;

import java.util.ArrayList;
import java.util.List;

public abstract class FlatComplexSentence implements Sentence {
    protected final List<Sentence> subSentences = new ArrayList<Sentence>();

    @Override
    public int getNumberSimplerSentences() {
        return subSentences.size();
    }

    @Override
    public Sentence getSimplerSentence(int i) {
        return subSentences.get(i);
    }

    @Override
    public boolean isNotSentence() {
        return false;
    }

    @Override
    public boolean isAndSentence() {
        return false;
    }

    @Override
    public boolean isOrSentence() {
        return false;
    }

    @Override
    public boolean isImplicationSentence() {
        return false;
    }

    @Override
    public boolean isBiconditionalSentence() {
        return false;
    }

    @Override
    public boolean isPropositionSymbol() {
        return false;
    }

    @Override
    public boolean isUnarySentence() {
        return false;
    }

    @Override
    public boolean isBinarySentence() {
        return false;
    }

    @Override
    public <A, R> R accept(PLVisitor<A, R> arplVisitor, A a) {
        return null;
    }

    @Override
    public String bracketSentenceIfNecessary(Connective connective, Sentence sentence) {
        return null;
    }

    public void add(Sentence sentence) {
        subSentences.add(sentence);
    }

}
