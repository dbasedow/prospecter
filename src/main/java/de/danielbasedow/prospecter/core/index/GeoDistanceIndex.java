package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.GeoUtil;
import de.danielbasedow.prospecter.core.geo.LatLng;

import java.util.*;

public class GeoDistanceIndex extends AbstractFieldIndex {
    /**
     * Tracks maximum distance seen during indexing. Allows reducing the area searched during matching
     */
    private int maxDistanceInIndex;
    private SortedMap<Integer, List<Long>> limitsWest;
    private SortedMap<Integer, List<Long>> limitsEast;
    private SortedMap<Integer, List<Long>> limitsNorth;
    private SortedMap<Integer, List<Long>> limitsSouth;
    private PostingAliasMap postings;

    public GeoDistanceIndex(String name) {
        super(name);
        maxDistanceInIndex = 0;
        limitsWest = new TreeMap<Integer, List<Long>>();
        limitsEast = new TreeMap<Integer, List<Long>>();
        limitsNorth = new TreeMap<Integer, List<Long>>();
        limitsSouth = new TreeMap<Integer, List<Long>>();
        postings = new PostingAliasMap();
    }

    @Override
    public List<QueryPosting> match(Field field) {
        List<QueryPosting> postingsFound = new ArrayList<QueryPosting>();
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
            for (Long aliasId : matcher.getMatches()) {
                postingsFound.add(postings.get(aliasId));
            }
        }
        return postingsFound;
    }

    @Override
    public void addPosting(Token token, QueryPosting posting) {
        Long aliasId = postings.aliasPosting(posting);
        GeoPerimeter perimeter = (GeoPerimeter) token.getToken();
        if (perimeter.getDistance() > maxDistanceInIndex) {
            maxDistanceInIndex = perimeter.getDistance();
        }
        writeToIndex(perimeter, aliasId);
        if (perimeter.spans180Longitude()) {
            aliasId = postings.aliasPosting(posting);
            writeToIndex(perimeter.mirrorInFakeSpace(), aliasId);
        }
    }

    private void writeToIndex(GeoPerimeter perimeter, Long aliasId) {
        addOrCreatePostings(limitsWest, perimeter.getWest(), aliasId);
        addOrCreatePostings(limitsEast, perimeter.getEast(), aliasId);
        addOrCreatePostings(limitsNorth, perimeter.getNorth(), aliasId);
        addOrCreatePostings(limitsSouth, perimeter.getSouth(), aliasId);
    }

    @Override
    public FieldType getFieldType() {
        return FieldType.GEO_DISTANCE;
    }

    private void addOrCreatePostings(SortedMap<Integer, List<Long>> index, Integer key, Long aliasId) {
        List<Long> postings;
        if (index.containsKey(key)) {
            postings = index.get(key);
        } else {
            postings = new ArrayList<Long>();
            index.put(key, postings);
        }
        postings.add(aliasId);
    }

    protected class GeoMatcher {
        /**
         * Maps aliasId to counter
         */
        private Map<Long, Byte> matches;

        /**
         * We have to match four times: east, west, north and south. Not necessarily in that order.
         * When discovering a match in a round greater one, we can ignore it if not already in matches table
         */
        private byte roundCount;

        public GeoMatcher() {
            matches = new HashMap<Long, Byte>();
            roundCount = 0;
        }

        public void matchIndex(SortedMap<Integer, List<Long>> index, Integer coordinate, Integer limit) {
            roundCount++;
            SortedMap<Integer, List<Long>> navigableMap;
            if (limit > coordinate) {
                navigableMap = index.subMap(coordinate, limit);
            } else {
                navigableMap = index.subMap(limit, coordinate);
            }
            if (navigableMap.size() > 0) {
                for (Map.Entry<Integer, List<Long>> entry : navigableMap.entrySet()) {
                    for (Long aliasId : entry.getValue()) {
                        recordMatch(aliasId);
                    }
                }
            }
        }

        private void recordMatch(Long aliasId) {
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

        public List<Long> getMatches() {
            if (roundCount < 4) {
                throw new IllegalStateException("It looks like the matching phase has not completed");
            }
            List<Long> aliases = new ArrayList<Long>();
            for (Map.Entry<Long, Byte> entry : matches.entrySet()) {
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
        private HashMap<Long, QueryPosting> postings;
        private long nextId;

        public PostingAliasMap() {
            postings = new HashMap<Long, QueryPosting>();
            nextId = 0;
        }

        /**
         * Gets an alias id and maps this id to the query posting. Calling this with the same posting more than once
         * results in multiple aliases mapping to the same posting.
         *
         * @param posting posting to alias
         * @return alias id
         */
        public Long aliasPosting(QueryPosting posting) {
            Long id = nextId;
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
        public QueryPosting get(Long id) {
            return postings.get(id);
        }
    }
}
