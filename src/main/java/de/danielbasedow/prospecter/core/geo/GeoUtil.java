package de.danielbasedow.prospecter.core.geo;

/**
 * In indices prospecter uses Integers to represent the latitude and longitude of coordinates.
 *
 * This class provides methods to switch between the Double and Integer representation.
 *
 * To
 */
public class GeoUtil {
    public static int latitudeToInt(double latitude) {
        if (latitude < -90) {
            latitude = -90;
        } else if (latitude > 90) {
            latitude = 90;
        }
        int result = 0;

        if (latitude < 0) {
            result = (int) ((Integer.MIN_VALUE / 90.0) * Math.abs(latitude));
        }
        if (latitude > 0) {
            result = (int) ((Integer.MAX_VALUE / 90.0) * Math.abs(latitude));
        }
        return result;
    }

    public static int longitudeToInt(double longitude) {
        if (longitude < -360) {
            longitude = -360;
        } else if (longitude > 360) {
            longitude = 360;
        }

        int result = 0;

        if (longitude < 0) {
            result = (int) ((Integer.MIN_VALUE / 360) * Math.abs(longitude));
        }
        if (longitude > 0) {
            result = (int) ((Integer.MAX_VALUE / 360) * Math.abs(longitude));
        }
        return result;
    }

    public static double longitudeToDouble(int longitude) {
        double result = 0;
        if (longitude < 0) {
            result = (-360.0 * longitude) / Integer.MIN_VALUE;
        } else if (longitude > 0) {
            result = (360.0 * longitude) / Integer.MAX_VALUE;
        }
        return result;
    }

    public static double latitudeToDouble(int latitude) {
        double result = 0;
        if (latitude < 0) {
            result = (-90.0 * latitude) / Integer.MIN_VALUE;
        } else if (latitude > 0) {
            result = (90.0 * latitude) / Integer.MAX_VALUE;
        }
        return result;
    }
}
