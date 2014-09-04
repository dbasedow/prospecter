package de.danielbasedow.prospecter.core.geo;

import junit.framework.TestCase;

public class GeoPerimeterTest extends TestCase {
    public void testZeroDistance() {
        GeoPerimeter perimeter = new GeoPerimeter(53.55, 10, 0);
        assertEquals(perimeter.getSouthDouble(), 53.55);
        assertEquals(perimeter.getNorthDouble(), 53.55);
        assertEquals(perimeter.getEastDouble(), 10.0);
        assertEquals(perimeter.getWestDouble(), 10.0);
    }
}
