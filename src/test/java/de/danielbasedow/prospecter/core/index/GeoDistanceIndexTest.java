package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Matcher;
import de.danielbasedow.prospecter.core.query.QueryManager;
import de.danielbasedow.prospecter.core.query.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.LatLng;
import gnu.trove.list.TLongList;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

public class GeoDistanceIndexTest extends TestCase {
    public void testMatching() {
        GeoDistanceIndex index = new GeoDistanceIndex("foo");
        GeoPerimeter perimeter = new GeoPerimeter(53.55, 10, 100000); //Hamburg, Germany +100km
        Token<GeoPerimeter> t = new Token<GeoPerimeter>(perimeter);
        index.addPosting(t, QueryPosting.pack(1, 1, false));

        LatLng latLng = new LatLng(53.866, 10.684); //Lübeck, about 65km north-east of Hamburg
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<LatLng>(latLng));
        Field field = new Field("foo", tokens);

        Matcher matcher = new Matcher(new QueryManager());
        index.match(field, matcher);
        assertEquals(1, matcher.getPositiveMatchCount());
    }

    public void testWesternWrap() {
        GeoDistanceIndex index = new GeoDistanceIndex("foo");
        GeoPerimeter perimeter = new GeoPerimeter(53.55, -179.98, 100000); //somewhere in Russia +100km
        Token<GeoPerimeter> t = new Token<GeoPerimeter>(perimeter);
        index.addPosting(t, QueryPosting.pack(1, 1, false));

        LatLng latLng = new LatLng(53.55, 179.98); //also somewhere in Russia
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<LatLng>(latLng));
        Field field = new Field("foo", tokens);

        Matcher matcher = new Matcher(new QueryManager());
        index.match(field, matcher);
        assertEquals(1, matcher.getPositiveMatchCount());
    }

    public void testEasternWrap() {
        GeoDistanceIndex index = new GeoDistanceIndex("foo");
        GeoPerimeter perimeter = new GeoPerimeter(53.55, 179.98, 100000); //somewhere in Russia +100km
        Token<GeoPerimeter> t = new Token<GeoPerimeter>(perimeter);
        index.addPosting(t, QueryPosting.pack(1, 1, false));

        LatLng latLng = new LatLng(53.55, -179.98); //also somewhere in Russia
        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<LatLng>(latLng));
        Field field = new Field("foo", tokens);

        Matcher matcher = new Matcher(new QueryManager());
        index.match(field, matcher);
        assertEquals(1, matcher.getPositiveMatchCount());
    }
}
