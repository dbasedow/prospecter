package de.danielbasedow.prospecter.core;

import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import de.danielbasedow.prospecter.core.query.build.*;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class PropositionSentenceMapperTest extends TestCase {
    public void test() {
        List<ClauseNode> conditionsA = new ArrayList<ClauseNode>();
        conditionsA.add(new Condition<Integer>("price", "gte", new Value<Integer>(100)));
        conditionsA.add(new Condition<Integer>("floor", "lt", new Value<Integer>(4)));
        Clause clauseA = new Clause(Clause.ClauseType.AND, conditionsA);

        List<ClauseNode> conditionsB = new ArrayList<ClauseNode>();
        conditionsB.add(new Condition<String>("category", "eq", new Value<String>("foo")));
        conditionsB.add(new Condition<String>("category", "eq", new Value<String>("bar")));
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
