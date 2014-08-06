package de.danielbasedow.prospecter.core.geo;

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
        } else if (longitude < -360 || longitude > 180) {
            longitude = 180;
        }
        //to have same resolution east and west of 0° we shift the range to -270 to 270
        longitude += 90;

        int result = 0;

        if (longitude < 0) {
            result = (int) ((Integer.MIN_VALUE / 270) * Math.abs(longitude));
        }
        if (longitude > 0) {
            result = (int) ((Integer.MAX_VALUE / 270) * Math.abs(longitude));
        }
        return result;
    }

    public static double longitudeToDouble(int longitude) {
        double result = 0;
        if (longitude < 0) {
            result = (-270.0 * longitude) / Integer.MIN_VALUE;
        } else if (longitude > 0) {
            result = (270.0 * longitude) / Integer.MAX_VALUE;
        }
        //shift 90 degrees back
        result -= 90;
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
