package de.danielbasedow.prospecter.core.index;

import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.GeoUtil;
import de.danielbasedow.prospecter.core.geo.LatLng;

import java.util.*;

public class GeoDistanceIndex extends AbstractFieldIndex {
    private int maxDistanceInIndex;
    private SortedMap<Integer, List<Long>> limitsWest;
    private SortedMap<Integer, List<Long>> limitsEast;
    private SortedMap<Integer, List<Long>> limitsNorth;
    private SortedMap<Integer, List<Long>> limitsSouth;
    private Map<Long, QueryPosting> postings;

    public GeoDistanceIndex(String name) {
        super(name);
        maxDistanceInIndex = 0;
        limitsWest = new TreeMap<Integer, List<Long>>();
        limitsEast = new TreeMap<Integer, List<Long>>();
        limitsNorth = new TreeMap<Integer, List<Long>>();
        limitsSouth = new TreeMap<Integer, List<Long>>();
        postings = new HashMap<Long, QueryPosting>();

        postings = new HashMap<Long, QueryPosting>();

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
            for (Long queryId : matcher.getMatches()) {
                postingsFound.add(postings.get(queryId));
            }
        }
        return postingsFound;
    }

    @Override
    public void addPosting(Token token, QueryPosting posting) {
        if (postings.containsKey(posting.getQueryId())) {
            //TODO: remove this limitation (don't just use queryId but bit position as well)
            throw new UnsupportedOperationException("Multiple geo distance queries in one query are currently not supported");
        }
        postings.put(posting.getQueryId(), posting);
        GeoPerimeter perimeter = (GeoPerimeter) token.getToken();
        if (perimeter.getDistance() > maxDistanceInIndex) {
            maxDistanceInIndex = perimeter.getDistance();
        }
        addOrCreatePostings(limitsWest, perimeter.getWest(), posting.getQueryId());
        addOrCreatePostings(limitsEast, perimeter.getEast(), posting.getQueryId());
        addOrCreatePostings(limitsNorth, perimeter.getNorth(), posting.getQueryId());
        addOrCreatePostings(limitsSouth, perimeter.getSouth(), posting.getQueryId());
    }

    private void addOrCreatePostings(SortedMap<Integer, List<Long>> index, Integer key, Long queryId) {
        List<Long> postings;
        if (index.containsKey(key)) {
            postings = index.get(key);
        } else {
            postings = new ArrayList<Long>();
            index.put(key, postings);
        }
        postings.add(queryId);
    }

    protected class GeoMatcher {
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
                    for (Long queryId : entry.getValue()) {
                        recordMatch(queryId);
                    }
                }
            }
        }

        private void recordMatch(Long queryId) {
            Byte matchCount;
            if (matches.containsKey(queryId)) {
                matchCount = matches.get(queryId);
                matchCount++;
            } else {
                //Only create new entries in first round
                if (roundCount == 1) {
                    matchCount = (byte) 1;
                } else {
                    return;
                }
            }
            matches.put(queryId, matchCount);
        }

        public List<Long> getMatches() {
            if (roundCount < 4) {
                throw new IllegalStateException("It looks like the matching phase has not completed");
            }
            List<Long> queries = new ArrayList<Long>();
            for (Map.Entry<Long, Byte> entry : matches.entrySet()) {
                if (entry.getValue() == roundCount) {
                    queries.add(entry.getKey());
                }
            }
            return queries;
        }

    }
}
