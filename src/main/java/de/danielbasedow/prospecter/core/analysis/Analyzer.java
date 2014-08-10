package de.danielbasedow.prospecter.core.analysis;

import de.danielbasedow.prospecter.core.Token;

import java.util.List;

/**
 * Interface for Analyzers. Analyzers turn raw text into a list of tokens.
 */
public interface Analyzer {
    /**
     * Tokenizes raw input
     *
     * @param input raw String that should be turned into tokens
     * @return list of tokens
     * @throws TokenizerException
     */
    public List<Token> tokenize(String input) throws TokenizerException;

    /**
     * Tokenizes raw input. It is possible to turn off generating formerly unknown tokens. This makes sense when
     * tokenizing documents, as any token in a document has to have been already seen in a query to have any chance
     * of matching.
     *
     * @param input raw String that should be turned into tokens
     * @param dontGenerateNewIds if set to true no new tokens will be generated.
     * @return list of tokens
     * @throws TokenizerException
     */
    public List<Token> tokenize(String input, boolean dontGenerateNewIds) throws TokenizerException;
}
