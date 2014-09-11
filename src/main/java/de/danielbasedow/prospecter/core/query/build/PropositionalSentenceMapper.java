package de.danielbasedow.prospecter.core.query.build;


import aima.core.logic.propositional.parsing.ast.ComplexSentence;
import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import de.danielbasedow.prospecter.core.query.Condition;

import java.util.ArrayList;

public class PropositionalSentenceMapper {
    public static Sentence map(ClauseNode clause) {
        if (!clause.isLeaf()) {
            return mapAsComplexSentence((Clause) clause);
        } else {
            return mapAsAtomicSentence((Condition) clause);
        }
    }

    private static Sentence mapAsAtomicSentence(Condition clause) {
        return new PropositionSymbol(clause);
    }

    private static Sentence mapAsComplexSentence(Clause clause) {
        ArrayList<Sentence> subSentences = new ArrayList<Sentence>();
        for (ClauseNode clauseNode : clause.getSubClauses()) {
            subSentences.add(map(clauseNode));
        }

        Connective connective;
        switch (clause.getType()) {
            case AND:
                connective = Connective.AND;
                break;
            case OR:
                connective = Connective.OR;
                break;
            case NOT:
                connective = Connective.NOT;
                break;
            default:
                connective = Connective.AND;
        }

        Sentence[] sentenceArray = new Sentence[subSentences.size()];
        return new ComplexSentence(connective, subSentences.toArray(sentenceArray));
    }
}
