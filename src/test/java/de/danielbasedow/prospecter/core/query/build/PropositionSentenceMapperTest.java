package de.danielbasedow.prospecter.core.query.build;

import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.query.Condition;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class PropositionSentenceMapperTest extends TestCase {
    public void test() {
        List<ClauseNode> conditionsA = new ArrayList<ClauseNode>();
        conditionsA.add(new Condition("price", new Token<Integer>(100, MatchCondition.GREATER_THAN_EQUALS)));
        conditionsA.add(new Condition("floor", new Token<Integer>(4, MatchCondition.LESS_THAN)));
        Clause clauseA = new Clause(Clause.ClauseType.AND, conditionsA);

        List<ClauseNode> conditionsB = new ArrayList<ClauseNode>();
        conditionsB.add(new Condition("category", new Token<String>("foo", MatchCondition.EQUALS)));
        conditionsB.add(new Condition("category", new Token<String>("bar", MatchCondition.EQUALS)));
        Clause clauseB = new Clause(Clause.ClauseType.AND, conditionsB);

        List<ClauseNode> clauses = new ArrayList<ClauseNode>();
        clauses.add(clauseA);
        clauses.add(clauseB);

        Clause root = new Clause(Clause.ClauseType.OR, clauses);
        Sentence sentence = PropositionalSentenceMapper.map(root);
        assertEquals(Connective.OR, sentence.getConnective());
        assertEquals(2, sentence.getNumberSimplerSentences());
    }
}
