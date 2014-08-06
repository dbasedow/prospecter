package de.danielbasedow.prospecter.core.geo;

import junit.framework.TestCase;

public class GeoUtilTest extends TestCase {
    public void testLatToInt() {
        assertEquals(0, GeoUtil.latitudeToInt(0));
        assertTrue(GeoUtil.latitudeToInt(-1) < 0);
        assertTrue(GeoUtil.latitudeToInt(-2) < GeoUtil.latitudeToInt(-1));

        assertTrue(GeoUtil.latitudeToInt(1) > 0);
        assertTrue(GeoUtil.latitudeToInt(2) > GeoUtil.latitudeToInt(1));

        assertEquals(238, GeoUtil.latitudeToInt(0.00001));
        assertEquals(-238, GeoUtil.latitudeToInt(-0.00001));

        assertEquals(2147483408, GeoUtil.latitudeToInt(89.99999));
        assertTrue(GeoUtil.latitudeToInt(90.000001) == GeoUtil.latitudeToInt(100.0));

        assertEquals(-2147483409, GeoUtil.latitudeToInt(-89.99999));
        assertTrue(GeoUtil.latitudeToInt(-90.000001) == GeoUtil.latitudeToInt(-100.0));

        assertFalse(GeoUtil.latitudeToInt(-100.0) == GeoUtil.latitudeToInt(100.0));
    }


    public void testLngToInt() {
        assertTrue(GeoUtil.longitudeToInt(-2) < GeoUtil.longitudeToInt(-1));
        assertTrue(GeoUtil.longitudeToInt(2) > GeoUtil.longitudeToInt(1));

        assertEquals(715827949, GeoUtil.longitudeToInt(0.00001));
        assertEquals(715827790, GeoUtil.longitudeToInt(-0.00001));

        assertEquals(2147483530, GeoUtil.longitudeToInt(179.99999));
        assertTrue(GeoUtil.longitudeToInt(180.000001) == GeoUtil.longitudeToInt(270.0));

        assertEquals(-2147483530, GeoUtil.longitudeToInt(-359.99999));
        assertTrue(GeoUtil.longitudeToInt(-360.000001) == GeoUtil.longitudeToInt(-361.0));
    }


    public void testLngToDbl() {
        //should be 0.00001 but obviously some precision is lost
        assertTrue(0.000012 > GeoUtil.longitudeToDouble(715827949));
        assertTrue(0.000008 < GeoUtil.longitudeToDouble(715827949));

        //should be 179.99999
        assertTrue(180.0 > GeoUtil.longitudeToDouble(2147483530));
        assertTrue(179.9999 < GeoUtil.longitudeToDouble(2147483530));
    }


    public void testLatToDbl() {
        //should be 0.00001 but obviously some precision is lost
        assertTrue(0.000012 > GeoUtil.latitudeToDouble(238));
        assertTrue(0.000008 < GeoUtil.latitudeToDouble(238));

        //should be 89.99999
        assertTrue(90.0 > GeoUtil.latitudeToDouble(2147483408));
        assertTrue(89.9999 < GeoUtil.latitudeToDouble(2147483408));

        //should be -89.99999
        assertTrue(-90.0 < GeoUtil.latitudeToDouble(-2147483409));
        assertTrue(89.9999 > GeoUtil.latitudeToDouble(-2147483409));
    }
}
