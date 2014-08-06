package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Query;
import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.LatLng;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class GeoDistanceIndexTest extends TestCase {
    public void testMatching() {
        GeoDistanceIndex index = new GeoDistanceIndex("foo");
        GeoPerimeter perimeter = new GeoPerimeter(53.55, 10, 100000); //Hamburg, Germany +100km
        Token<GeoPerimeter> t = new Token<GeoPerimeter>(perimeter);
        QueryPosting qp = new QueryPosting(1, (short) 1);
        index.addPosting(t, qp);

        LatLng latLng = new LatLng(53.866, 10.684); //Lübeck, about 65km north-east of Hamburg
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<LatLng>(latLng));
        Field field = new Field("foo", tokens);

        List<QueryPosting> postings = index.match(field);
        assertEquals(1, postings.size());
    }
}