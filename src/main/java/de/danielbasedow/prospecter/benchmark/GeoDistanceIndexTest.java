package de.danielbasedow.prospecter.benchmark;

import de.danielbasedow.prospecter.core.MatchCondition;
import de.danielbasedow.prospecter.core.QueryPosting;
import de.danielbasedow.prospecter.core.Token;
import de.danielbasedow.prospecter.core.document.Field;
import de.danielbasedow.prospecter.core.geo.GeoPerimeter;
import de.danielbasedow.prospecter.core.geo.LatLng;
import de.danielbasedow.prospecter.core.index.GeoDistanceIndex;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GeoDistanceIndexTest {
    public static void main(String[] args) {
        GeoDistanceIndex index = new GeoDistanceIndex("foo");
        System.out.print((new Date()).getTime());
        System.out.println(" start filling index");
        fillIndex(index, 1000000, 5.5, 15.0, 55.0, 47.0, 100000);
        System.out.print((new Date()).getTime());
        System.out.println(" done filling index");

        List<Token> tokens = new ArrayList<Token>();
        tokens.add(new Token<LatLng>(new LatLng(53.55, 10)));

        System.out.print((new Date()).getTime());
        System.out.println(" start matching");
        List<QueryPosting> postings = index.match(new Field("foo", tokens));
        System.out.print((new Date()).getTime());
        System.out.println(" done matching");
        System.out.println("Matched: " + Integer.toString(postings.size()));
        printVMStats();
    }

    private static void fillIndex(GeoDistanceIndex index, int count, double west, double east, double north, double south, int maxDistance) {
        for (int i = 0; i < count; i++) {
            GeoPerimeter perimeter = getRandomLatLng(west, east, north, south, maxDistance);
            Token<GeoPerimeter> token = new Token<GeoPerimeter>(perimeter);
            index.addPosting(token, new QueryPosting(i, (short) 1));
        }
    }

    private static GeoPerimeter getRandomLatLng(double west, double east, double north, double south, int maxDistance) {
        double latitude = Math.random() * (north - south) + south;
        double longitude = Math.random() * (east - west) + west;
        return new GeoPerimeter(latitude, longitude, (int) (Math.random() * maxDistance));
    }

    public static void printVMStats() {
        Runtime runtime = Runtime.getRuntime();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
        runtime.gc();
        System.out.println("used: " + (runtime.totalMemory() - runtime.freeMemory()));
    }
}
