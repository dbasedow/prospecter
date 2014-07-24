package de.danielbasedow.prospecter.core;

import com.google.inject.Guice;
import com.google.inject.Injector;
import de.danielbasedow.prospecter.core.index.FullTextIndex;
import junit.framework.TestCase;

public class MatcherTest extends TestCase {
    private Injector injector;

    public void setUp() {
        injector = Guice.createInjector(new ProspecterModule());
    }

    public void test() {
        FullTextIndex ft = new FullTextIndex();
        QueryPosting posting = new QueryPosting(1, (short) 1);
        ft.addPosting(1, posting);
        Matcher m = injector.getInstance(Matcher.class);
        m.collectHits(ft, new Integer[]{1});
        assertEquals(1, m.hits.size());
    }
}
