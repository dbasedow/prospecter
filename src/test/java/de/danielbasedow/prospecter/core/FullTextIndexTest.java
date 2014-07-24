package de.danielbasedow.prospecter.core;

import de.danielbasedow.prospecter.core.FullTextIndex;
import de.danielbasedow.prospecter.core.QueryPosting;
import junit.framework.TestCase;

public class FullTextIndexTest extends TestCase {
    public void test() {
        FullTextIndex ft = new FullTextIndex();
        assertEquals(0, ft.index.size());
        QueryPosting posting = new QueryPosting(1, (short) 1);
        ft.addPosting(1, posting);
        assertEquals(1, ft.index.size());
    }
}
