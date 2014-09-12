package de.danielbasedow.prospecter.core.query;

import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.query.build.Clause;
import de.danielbasedow.prospecter.core.query.build.ClauseNode;
import de.danielbasedow.prospecter.core.query.build.Conjunction;
import de.danielbasedow.prospecter.core.query.build.PropositionalSentenceMapper;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class QueryTest extends TestCase {
    public void testFlattenOr() {
        List<ClauseNode> conditionsA = new ArrayList<ClauseNode>();
        conditionsA.add(new Condition("price", new Token<Integer>(100, MatchCondition.GREATER_THAN_EQUALS)));
        conditionsA.add(new Condition("floor", new Token<Integer>(4, MatchCondition.LESS_THAN)));
        Clause clauseA = new Clause(Clause.ClauseType.OR, conditionsA);

        List<ClauseNode> conditionsB = new ArrayList<ClauseNode>();
        conditionsB.add(new Condition("category", new Token<String>("foo", MatchCondition.EQUALS)));
        conditionsB.add(new Condition("category", new Token<String>("bar", MatchCondition.EQUALS)));
        Clause clauseB = new Clause(Clause.ClauseType.OR, conditionsB);

        List<ClauseNode> clauses = new ArrayList<ClauseNode>();
        clauses.add(clauseA);
        clauses.add(clauseB);

        Clause root = new Clause(Clause.ClauseType.OR, clauses);
        Sentence sentence = PropositionalSentenceMapper.map(root);
        assertEquals(Connective.OR, sentence.getConnective());
        assertEquals(2, sentence.getNumberSimplerSentences());

        Sentence cnf = Query.getCNF(sentence);
        Conjunction conjunction = new Conjunction();
        Query.flatten(conjunction, cnf);
        assertEquals(1, conjunction.getNumberSimplerSentences());
        assertEquals(4, conjunction.getSimplerSentence(0).getNumberSimplerSentences());
    }
}
