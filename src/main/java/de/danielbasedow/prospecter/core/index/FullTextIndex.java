package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;

import java.util.ArrayList;
import java.util.HashMap;

public class FullTextIndex {
    protected HashMap<Integer, ArrayList<QueryPosting>> index;

    public FullTextIndex() {
        index = new HashMap<Integer, ArrayList<QueryPosting>>();
    }

    public void addPosting(Integer termId, QueryPosting posting) {
        ArrayList<QueryPosting> postingList;
        if (index.containsKey(termId)) {
            postingList = index.get(termId);
        } else {
            postingList = new ArrayList<QueryPosting>();
            index.put(termId, postingList);
        }
        postingList.add(posting);
    }

    public ArrayList<QueryPosting> getQueryPostingsForTermId(Integer termId) {
        if (index.containsKey(termId)) {
            return index.get(termId);
        }
        return null;
    }
}
