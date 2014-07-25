package de.danielbasedow.prospecter.core.analysis;


import java.util.ArrayList;
import java.util.Arrays;

public class TokenizerImpl implements Tokenizer {
    @Override
    public ArrayList<String> tokenize(String input) {
        return new ArrayList<String>(Arrays.asList(input.split("\\s")));
    }
}
