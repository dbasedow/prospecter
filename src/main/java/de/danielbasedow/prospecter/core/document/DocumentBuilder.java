package de.danielbasedow.prospecter.core.document;


import com.google.inject.Inject;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;

import java.util.HashMap;
import java.util.List;

public class DocumentBuilder {
    private Analyzer analyzer;

    @Inject
    public DocumentBuilder(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public Document build(HashMap<String, String> rawFields) {
        //TODO: don't generate termids for terms not already present. they can never match anyway!
        Document document = new DocumentImpl();
        for (String key : rawFields.keySet()) {
            List<Token> termIds = analyzer.tokenize(rawFields.get(key));
            Field f = new Field(key, termIds);
            document.addField(key, f);
        }
        return document;
    }
}
