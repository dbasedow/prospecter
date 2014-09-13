package de.danielbasedow.prospecter.core.query;

import aima.core.logic.propositional.parsing.ast.Connective;
import aima.core.logic.propositional.parsing.ast.Sentence;
import aima.core.logic.propositional.visitors.ConvertToCNF;
import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.query.build.*;

import java.util.*;

/**
 * represents a Query (a tuple of queryId, bitmask and a list of conditions)
 */
public class Query {
    protected final int queryId;
    protected final int bits;
    protected final ClauseNode clauseNode;
    protected final Map<Condition, Long> postings = new HashMap<Condition, Long>();

    public int getQueryId() {
        return queryId;
    }

    public Query(int queryId, ClauseNode clauseNode) {
        this.clauseNode = clauseNode;
        this.queryId = queryId;

        Sentence cnf = getCNF(clauseNode);

        int tmpBits = 0;
        for (int bit = 0; bit < cnf.getNumberSimplerSentences(); bit++) {
            tmpBits++;
            Sentence disjunction = cnf.getSimplerSentence(bit);
            for (int p = 0; p < disjunction.getNumberSimplerSentences(); p++) {
                Condition condition = ((PropositionSymbol) disjunction.getSimplerSentence(p)).getCondition();
                if (condition.getToken().getCondition() == MatchCondition.IN) {
                    //If this is an IN query we're dealing with a Token containing a List<Token>
                    Object t = condition.getToken().getToken();
                    if (t instanceof List) {
                        for (Token token : (List<Token>) t) {
                            Condition tmpCondition = new Condition(condition.getFieldName(), token);
                            postings.put(tmpCondition, QueryPosting.pack(queryId, bit));
                        }
                    }
                } else {
                    postings.put(condition, QueryPosting.pack(queryId, bit));
                }
            }
        }
        bits = tmpBits;
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

    public ClauseNode getClauses() {
        return clauseNode;
    }

    public int getBits() {
        return bits;
    }
}
