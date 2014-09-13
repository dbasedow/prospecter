package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.LatLng;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.procedure.TLongProcedure;
import net.sf.jsi.SpatialIndex;
import net.sf.jsi.rtree.RTree;

import java.util.*;

/**
 * Enables geo distance search queries.
 * <p/>
 * Indexing a geo distance Token results in entries in sorted maps for all four cardinal directions (N, E, S, W).
 * <p/>
 * To reduce range searches and simplifying calculations this implementation add 360° to the earth's longitudes. As in
 * reality, 180W equals 180E. Additionally 360E equals 180W and 360W. Same goes for the other direction.
 * <p/>
 * This allows simple calculations of eastern and western limits by adding or subtracting from the center. For searches
 * spanning 180° a second posting in all maps is added on the other side of the system.
 */
public class GeoDistanceIndex extends AbstractFieldIndex {
    /**
     * Tracks maximum distance seen during indexing. Allows reducing the area searched during matching
     */
    private final SpatialIndex index;

    public GeoDistanceIndex(String name) {
        super(name);
        index = new RTree();
        index.init(null);
    }

    @Override
    public TLongArrayList match(Field field) {
        TLongArrayList postingsFound = new TLongArrayList();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            LatLng latLng = (LatLng) token.getToken();
            GeoPerimeter perimeter = new GeoPerimeter(latLng.getLatitude(), latLng.getLongitude(), 0);
            index.intersects(perimeter.getRectangle(), new MatchCollectionProcedure(postingsFound));
        }
        return postingsFound;
    }

    @Override
    public void addPosting(Token token, Long posting, boolean not) {
        GeoPerimeter perimeter = (GeoPerimeter) token.getToken();
        index.add(perimeter.getRectangle(), posting);
        if (perimeter.spans180Longitude()) {
            //if it spans 180° add fake posting on other side of earth
            GeoPerimeter bizarroPerimeter = perimeter.mirrorInFakeSpace();
            index.add(bizarroPerimeter.getRectangle(), posting);
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.GEO_DISTANCE;
    }

    private class MatchCollectionProcedure implements TLongProcedure {
        private final TLongArrayList hits;

        public MatchCollectionProcedure(TLongArrayList hitList) {
            hits = hitList;
        }

        @Override
        public boolean execute(long i) {
            hits.add(i);
            return true;
        }
    }
}
