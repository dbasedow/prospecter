package de.danielbasedow.prospecter.core.geo;

import junit.framework.TestCase;

public class GeoPerimeterTest extends TestCase {
    public void testZeroDistance() {
        GeoPerimeter perimeter = new GeoPerimeter(53.55, 10, 0);
        assertEquals(perimeter.getSouth(), 53.55f);
        assertEquals(perimeter.getNorth(), 53.55f);
        assertEquals(perimeter.getEast(), 10.0f);
        assertEquals(perimeter.getWest(), 10.0f);
    }
}
