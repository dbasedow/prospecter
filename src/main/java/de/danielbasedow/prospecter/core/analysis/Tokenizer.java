package de.danielbasedow.prospecter.core.analysis;

import java.util.List;

public interface Tokenizer {
    public List<String> tokenize(String input);
}
