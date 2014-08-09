package de.danielbasedow.prospecter.core.document;


import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.analysis.Analyzer;
import de.danielbasedow.prospecter.core.analysis.TokenizerException;
import de.danielbasedow.prospecter.core.index.FieldIndex;
import de.danielbasedow.prospecter.core.index.FieldType;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import de.danielbasedow.prospecter.core.schema.Schema;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashMap;
import java.util.List;

public class DocumentBuilder {
    private Schema schema;

    public DocumentBuilder(Schema schema) {
        this.schema = schema;
    }

    public Document build(HashMap<String, String> rawFields) {
        Document document = new DocumentImpl();
        for (String key : rawFields.keySet()) {
            FieldIndex fieldIndex = schema.getFieldIndex(key);
            try {
                if (fieldIndex != null) {
                    if (fieldIndex.getFieldType() == FieldType.FULL_TEXT) {
                        Analyzer analyzer = ((FullTextIndex) fieldIndex).getAnalyzer();
                        List<Token> termIds = analyzer.tokenize(rawFields.get(key), true);
                        Field f = new Field(key, termIds);
                        document.addField(key, f);
                    } else {
                        throw new NotImplementedException();
                    }
                }
            } catch (TokenizerException e) {
                e.printStackTrace();
            }
        }
        return document;
    }
}
