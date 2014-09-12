package de.danielbasedow.prospecter.core.query;

import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.logic.propositional.visitors.ConvertToCNF;
import de.danielbasedow.prospecter.core.query.build.*;

import java.util.*;

/**
 * represents a Query (a tuple of queryId, bitmask and a list of conditions)
 */
public class Query {
    protected final Integer queryId;
    protected final BitSet mask;
    protected final ClauseNode clauseNode;
    protected final Map<Condition, Long> postings = new HashMap<Condition, Long>();

    public Integer getQueryId() {
        return queryId;
    }

    public Query(Integer queryId, ClauseNode clauseNode) {
        this.clauseNode = clauseNode;
        this.queryId = queryId;

        Sentence cnf = getCNF(clauseNode);
        mask = new BitSet(cnf.getNumberSimplerSentences());

        for (int bit = 0; bit < cnf.getNumberSimplerSentences(); bit++) {
            Sentence disjunction = cnf.getSimplerSentence(bit);
            for (int p = 0; p < disjunction.getNumberSimplerSentences(); p++) {
                Condition condition = ((PropositionSymbol) disjunction.getSimplerSentence(p)).getCondition();
                postings.put(condition, QueryPosting.pack(queryId, bit));
            }
        }
    }

    public static Sentence getCNF(Sentence sentence) {
        Conjunction conjunction = new Conjunction();
        flatten(conjunction, ConvertToCNF.convert(sentence));
        return conjunction;
    }

    public static Sentence getCNF(ClauseNode clauseNode) {
        return getCNF(PropositionalSentenceMapper.map(clauseNode));
    }

    public static void flatten(Conjunction conjunctionCollector, Sentence sentence) {
        if (sentence.getNumberSimplerSentences() > 0 && sentence.getConnective() == Connective.AND) {
            for (int i = 0; i < sentence.getNumberSimplerSentences(); i++) {
                flatten(conjunctionCollector, sentence.getSimplerSentence(i));
            }
        } else {
            Disjunction disjunctionCollector = new Disjunction();
            flatten(disjunctionCollector, sentence);
            conjunctionCollector.add(disjunctionCollector);
        }
    }

    public static void flatten(Disjunction disjunctionCollector, Sentence sentence) {
        if (sentence.getNumberSimplerSentences() > 0 && sentence.getConnective() == Connective.OR) {
            for (int i = 0; i < sentence.getNumberSimplerSentences(); i++) {
                flatten(disjunctionCollector, sentence.getSimplerSentence(i));
            }
        } else {
            disjunctionCollector.add(sentence);
        }
    }

    /**
     * Get QueryPostings for every Condition
     *
     * @return map of Condition -> QueryPosting
     */
    public Map<Condition, Long> getPostings() {
        return postings;
    }

    public boolean testBits(BitSet hits) {
        return mask.equals(hits);
    }

    public ClauseNode getClauses() {
        return clauseNode;
    }

    public BitSet getMask() {
        return mask;
    }
}
