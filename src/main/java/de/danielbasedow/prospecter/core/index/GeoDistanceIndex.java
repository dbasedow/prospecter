package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.LatLng;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.procedure.TIntProcedure;
import net.sf.jsi.Rectangle;
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
    private SpatialIndex index;
    private PostingAliasMap postings;

    public GeoDistanceIndex(String name) {
        super(name);
        index = new RTree();
        index.init(null);
        postings = new PostingAliasMap();
    }

    @Override
    public TLongArrayList match(Field field) {
        TLongArrayList postingsFound = new TLongArrayList();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            LatLng latLng = (LatLng) token.getToken();
            GeoPerimeter perimeter = new GeoPerimeter(latLng.getLatitude(), latLng.getLongitude(), 0);
            MatchCollectionProcedure procedure = new MatchCollectionProcedure();
            index.intersects(new Rectangle(
                    perimeter.getWest(),
                    perimeter.getSouth(),
                    perimeter.getEast(),
                    perimeter.getNorth()
            ), procedure);

            for (Integer aliasId : procedure.getHits().toArray()) {
                postingsFound.add(postings.get(aliasId));
            }
        }
        return postingsFound;
    }

    @Override
    public void addPosting(Token token, Long posting) {
        Integer aliasId = postings.aliasPosting(posting);
        GeoPerimeter perimeter = (GeoPerimeter) token.getToken();
        index.add(new Rectangle(
                perimeter.getWest(),
                perimeter.getSouth(),
                perimeter.getEast(),
                perimeter.getNorth()
        ), aliasId);
        if (perimeter.spans180Longitude()) {
            //Move this to the matching phase to save memory and make aliases unnecessary
            //if it spans 180° add fake posting on other side of earth
            aliasId = postings.aliasPosting(posting);
            GeoPerimeter bizarroPerimeter = perimeter.mirrorInFakeSpace();
            index.add(new Rectangle(
                    bizarroPerimeter.getWest(),
                    bizarroPerimeter.getSouth(),
                    bizarroPerimeter.getEast(),
                    bizarroPerimeter.getNorth()
            ), aliasId);
        }
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.GEO_DISTANCE;
    }


    /**
     * A single QueryPosting can result in two index entries if it spans from hemispheres at 180/-180
     * PostingAliasMap supplies an alias id that allows looking up the QueryPosting
     */
    private class PostingAliasMap {
        private HashMap<Integer, Long> postings;
        private int nextId;

        public PostingAliasMap() {
            postings = new HashMap<Integer, Long>();
            nextId = 0;
        }

        /**
         * Gets an alias id and maps this id to the query posting. Calling this with the same posting more than once
         * results in multiple aliases mapping to the same posting.
         *
         * @param posting posting to alias
         * @return alias id
         */
        public Integer aliasPosting(Long posting) {
            Integer id = nextId;
            nextId++;
            postings.put(id, posting);
            return id;
        }

        /**
         * Get posting for alias id
         *
         * @param id alias id
         * @return aliased query posting
         */
        public long get(Integer id) {
            return postings.get(id);
        }
    }

    private class MatchCollectionProcedure implements TIntProcedure {
        private TIntArrayList hits;

        public MatchCollectionProcedure() {
            hits = new TIntArrayList();
        }

        @Override
        public boolean execute(int i) {
            hits.add(i);
            return true;
        }

        public TIntArrayList getHits() {
            return hits;
        }
    }
}
