package de.danielbasedow.prospecter.core.query.build;

import aima.core.logic.propositional.parsing.ast.AtomicSentence;

/**
 * Code copied from AIMA PropositionalSymbol. Only to change the isPropositionSymbol check.
 */
public class AbstractPropositionalSymbol extends AtomicSentence {
    //
    public static final String TRUE_SYMBOL = "True";
    public static final String FALSE_SYMBOL = "False";
    public static final AbstractPropositionalSymbol TRUE = new AbstractPropositionalSymbol(TRUE_SYMBOL);
    public static final AbstractPropositionalSymbol FALSE = new AbstractPropositionalSymbol(FALSE_SYMBOL);
    //
    protected String symbol;

    /**
     * Constructor.
     *
     * @param symbol the symbol uniquely identifying the proposition.
     */
    public AbstractPropositionalSymbol(String symbol) {
        // Ensure differing cases for the 'True' and 'False'
        // propositional constants are represented in a canonical form.
        if (TRUE_SYMBOL.equalsIgnoreCase(symbol)) {
            this.symbol = TRUE_SYMBOL;
        } else if (FALSE_SYMBOL.equalsIgnoreCase(symbol)) {
            this.symbol = FALSE_SYMBOL;
        } else if (isPropositionSymbol(symbol)) {
            this.symbol = symbol;
        } else {
            throw new IllegalArgumentException("Not a legal proposition symbol: " + symbol);
        }
    }

    /**
     * @return true if this is the always 'True' proposition symbol, false
     * otherwise.
     */
    public boolean isAlwaysTrue() {
        return TRUE_SYMBOL.equals(symbol);
    }

    /**
     * @return true if the symbol passed in is the always 'True' proposition
     * symbol, false otherwise.
     */
    public static boolean isAlwaysTrueSymbol(String symbol) {
        return TRUE_SYMBOL.equalsIgnoreCase(symbol);
    }

    /**
     * @return true if this is the always 'False' proposition symbol, false
     * other.
     */
    public boolean isAlwaysFalse() {
        return FALSE_SYMBOL.equals(symbol);
    }

    /**
     * @return true if the symbol passed in is the always 'False' proposition
     * symbol, false other.
     */
    public static boolean isAlwaysFalseSymbol(String symbol) {
        return FALSE_SYMBOL.equalsIgnoreCase(symbol);
    }

    /**
     * Determine if the given symbol is a legal proposition symbol.
     * This always returns true (not like the original)
     *
     * @param symbol a symbol to be tested.
     * @return true
     */
    public static boolean isPropositionSymbol(String symbol) {
        return true;
    }

    /**
     * Determine if the given character can be at the beginning of a proposition
     * symbol.
     *
     * @param ch a character.
     * @return true if the given character can be at the beginning of a
     * proposition symbol representation, false otherwise.
     */
    public static boolean isPropositionSymbolIdentifierStart(char ch) {
        return Character.isJavaIdentifierStart(ch);
    }

    /**
     * Determine if the given character is part of a proposition symbol.
     *
     * @param ch a character.
     * @return true if the given character is part of a proposition symbols
     * representation, false otherwise.
     */
    public static boolean isPropositionSymbolIdentifierPart(char ch) {
        return Character.isJavaIdentifierPart(ch);
    }

    /**
     * @return the symbol uniquely identifying the proposition.
     */
    public String getSymbol() {
        return symbol;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if ((o == null) || (this.getClass() != o.getClass())) {
            return false;
        }
        AbstractPropositionalSymbol sym = (AbstractPropositionalSymbol) o;
        return symbol.equals(sym.symbol);

    }

    @Override
    public int hashCode() {
        return symbol.hashCode();
    }

    @Override
    public String toString() {
        return getSymbol();
    }
}
