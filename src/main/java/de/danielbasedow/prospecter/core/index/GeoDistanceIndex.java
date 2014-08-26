package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.GeoUtil;
import de.danielbasedow.prospecter.core.geo.LatLng;
import gnu.trove.list.array.TLongArrayList;

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
    private int maxDistanceInIndex;
    private SortedMap<Integer, List<Integer>> limitsWest;
    private SortedMap<Integer, List<Integer>> limitsEast;
    private SortedMap<Integer, List<Integer>> limitsNorth;
    private SortedMap<Integer, List<Integer>> limitsSouth;
    private PostingAliasMap postings;

    public GeoDistanceIndex(String name) {
        super(name);
        maxDistanceInIndex = 0;
        limitsWest = new TreeMap<Integer, List<Integer>>();
        limitsEast = new TreeMap<Integer, List<Integer>>();
        limitsNorth = new TreeMap<Integer, List<Integer>>();
        limitsSouth = new TreeMap<Integer, List<Integer>>();
        postings = new PostingAliasMap();
    }

    @Override
    public TLongArrayList match(Field field) {
        TLongArrayList postingsFound = new TLongArrayList();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            LatLng latLng = (LatLng) token.getToken();
            GeoPerimeter perimeter = new GeoPerimeter(latLng.getLatitude(), latLng.getLongitude(), maxDistanceInIndex * 2);
            GeoMatcher matcher = new GeoMatcher();
            Integer intLongitude = GeoUtil.longitudeToInt(latLng.getLongitude());
            Integer intLatitude = GeoUtil.latitudeToInt(latLng.getLatitude());
            matcher.matchIndex(limitsEast, intLongitude, perimeter.getEast());
            matcher.matchIndex(limitsWest, intLongitude, perimeter.getWest());
            matcher.matchIndex(limitsNorth, intLatitude, perimeter.getNorth());
            matcher.matchIndex(limitsSouth, intLatitude, perimeter.getSouth());
            for (Integer aliasId : matcher.getMatches()) {
                postingsFound.add(postings.get(aliasId));
            }
        }
        return postingsFound;
    }

    @Override
    public void addPosting(Token token, Long posting) {
        Integer aliasId = postings.aliasPosting(posting);
        GeoPerimeter perimeter = (GeoPerimeter) token.getToken();
        if (perimeter.getDistance() > maxDistanceInIndex) {
            maxDistanceInIndex = perimeter.getDistance();
        }
        writeToIndex(perimeter, aliasId);
        if (perimeter.spans180Longitude()) {
            //if it spans 180° add fake posting on other side of earth
            aliasId = postings.aliasPosting(posting);
            writeToIndex(perimeter.mirrorInFakeSpace(), aliasId);
        }
    }

    private void writeToIndex(GeoPerimeter perimeter, Integer aliasId) {
        addOrCreatePostings(limitsWest, perimeter.getWest(), aliasId);
        addOrCreatePostings(limitsEast, perimeter.getEast(), aliasId);
        addOrCreatePostings(limitsNorth, perimeter.getNorth(), aliasId);
        addOrCreatePostings(limitsSouth, perimeter.getSouth(), aliasId);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.GEO_DISTANCE;
    }

    private void addOrCreatePostings(SortedMap<Integer, List<Integer>> index, Integer key, Integer aliasId) {
        List<Integer> postings;
        if (index.containsKey(key)) {
            postings = index.get(key);
        } else {
            postings = new ArrayList<Integer>();
            index.put(key, postings);
        }
        postings.add(aliasId);
    }

    /**
     * Special matcher used to find all postings that have ALL FOUR limits around the coordinate in the document field.
     * All four maps HAVE TO match to match the query posting.
     */
    protected class GeoMatcher {
        /**
         * Maps aliasId to counter
         */
        private Map<Integer, Byte> matches;

        /**
         * We have to match four times: east, west, north and south. Not necessarily in that order.
         * When discovering a match in a round greater one, we can ignore it if not already in matches table
         */
        private byte roundCount;

        public GeoMatcher() {
            matches = new HashMap<Integer, Byte>();
            roundCount = 0;
        }

        /**
         * Check SortedMap for entries between coordinate and limit.
         *
         * @param index      map to search
         * @param coordinate document fields longitude or latitude
         * @param limit      eastern, western, northern or southern limit
         */
        public void matchIndex(SortedMap<Integer, List<Integer>> index, Integer coordinate, Integer limit) {
            roundCount++;
            SortedMap<Integer, List<Integer>> navigableMap;
            if (limit > coordinate) {
                navigableMap = index.subMap(coordinate, limit);
            } else {
                navigableMap = index.subMap(limit, coordinate);
            }
            if (navigableMap.size() > 0) {
                for (Map.Entry<Integer, List<Integer>> entry : navigableMap.entrySet()) {
                    for (Integer aliasId : entry.getValue()) {
                        recordMatch(aliasId);
                    }
                }
            }
        }

        /**
         * Record match to keep track of how often a posting alias was encountered.
         *
         * @param aliasId matched alias id
         */
        private void recordMatch(Integer aliasId) {
            Byte matchCount;
            if (matches.containsKey(aliasId)) {
                matchCount = matches.get(aliasId);
                matchCount++;
            } else {
                //Only create new entries in first round
                if (roundCount == 1) {
                    matchCount = (byte) 1;
                } else {
                    return;
                }
            }
            matches.put(aliasId, matchCount);
        }

        /**
         * Get all aliases that have been matched exactly 4 times.
         *
         * @return matching aliases
         */
        public List<Integer> getMatches() {
            if (roundCount < 4) {
                throw new IllegalStateException("It looks like the matching phase has not completed");
            }
            List<Integer> aliases = new ArrayList<Integer>();
            for (Map.Entry<Integer, Byte> entry : matches.entrySet()) {
                if (entry.getValue() == roundCount) {
                    aliases.add(entry.getKey());
                }
            }
            return aliases;
        }
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
}
