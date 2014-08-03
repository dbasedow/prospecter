package de.danielbasedow.prospecter.core.analysis;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TokenizerImpl implements Tokenizer {
    @Override
    public List<String> tokenize(String input) {
        return new ArrayList<String>(Arrays.asList(input.split("\\s")));
    }
}
