package de.danielbasedow.prospecter.core.index;


import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class GeoDistanceIndex extends AbstractFieldIndex {
    private ConcurrentSkipListMap<Double, List<Long>> limitsWest;
    private ConcurrentSkipListMap<Double, List<Long>> limitsEast;
    private ConcurrentSkipListMap<Double, List<Long>> limitsNorth;
    private ConcurrentSkipListMap<Double, List<Long>> limitsSouth;
    private Map<Long, QueryPosting> postings;

    public GeoDistanceIndex(String name) {
        super(name);
        limitsWest = new ConcurrentSkipListMap<Double, List<Long>>();
        limitsEast = new ConcurrentSkipListMap<Double, List<Long>>();
        limitsNorth = new ConcurrentSkipListMap<Double, List<Long>>();
        limitsSouth = new ConcurrentSkipListMap<Double, List<Long>>();
        postings = new HashMap<Long, QueryPosting>();
    }

    @Override
    public List<QueryPosting> match(Field field) {
        List<QueryPosting> postingsFound = new ArrayList<QueryPosting>();
        List<Token> tokens = field.getTokens();
        for (Token token : tokens) {
            LatLng latLng = (LatLng) token.getToken();
            GeoMatcher matcher = new GeoMatcher();
            matcher.matchIndex(limitsEast, latLng.getLongitude(), MatchCondition.LESS_THAN);
            matcher.matchIndex(limitsWest, latLng.getLongitude(), MatchCondition.GREATER_THAN);
            matcher.matchIndex(limitsNorth, latLng.getLatitude(), MatchCondition.LESS_THAN);
            matcher.matchIndex(limitsSouth, latLng.getLatitude(), MatchCondition.GREATER_THAN);
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
        addOrCreatePostings(limitsWest, perimeter.getWest(), posting.getQueryId());
        addOrCreatePostings(limitsEast, perimeter.getEast(), posting.getQueryId());
        addOrCreatePostings(limitsNorth, perimeter.getNorth(), posting.getQueryId());
        addOrCreatePostings(limitsSouth, perimeter.getSouth(), posting.getQueryId());
    }

    private void addOrCreatePostings(ConcurrentSkipListMap<Double, List<Long>> index, Double key, Long queryId) {
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

        public void matchIndex(ConcurrentSkipListMap<Double, List<Long>> index, Double coordinate, MatchCondition condition) {
            roundCount++;
            ConcurrentNavigableMap<Double, List<Long>> navigableMap;
            if (condition == MatchCondition.GREATER_THAN) {
                navigableMap = index.headMap(coordinate);
            } else {
                navigableMap = index.tailMap(coordinate);
            }
            if (navigableMap.size() > 0) {
                for (Map.Entry<Double, List<Long>> entry : navigableMap.entrySet()) {
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
