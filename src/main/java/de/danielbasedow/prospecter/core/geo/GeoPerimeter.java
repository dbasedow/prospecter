package de.danielbasedow.prospecter.core.geo;

public class GeoPerimeter {
    public static final double EARTH_RADIUS = 6371009.0; //in meters
    public static final double LATITUDE_DEGREE_TO_METERS = 111195.0;

    private final double latitude;
    private final double longitude;
    private final int distance;

    /**
     * @param latitude  latitude of center
     * @param longitude longitude of center
     * @param distance  in meters
     */
    public GeoPerimeter(double latitude, double longitude, int distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public int getNorth() {
        return GeoUtil.latitudeToInt(latitude + (distance / LATITUDE_DEGREE_TO_METERS));
    }

    public int getSouth() {
        return GeoUtil.latitudeToInt(latitude - (distance / LATITUDE_DEGREE_TO_METERS));
    }

    public int getEast() {
        return GeoUtil.longitudeToInt(longitude + getLongitudeDegreeOffset());
    }

    public int getWest() {
        return GeoUtil.longitudeToInt(longitude - getLongitudeDegreeOffset());
    }

    public double getLongitudeDegreeOffset() {
        return Math.abs(
                distance / (
                        (2 * Math.PI * EARTH_RADIUS * Math.cos(latitude)) / 360
                )
        );
    }

    public int getDistance() {
        return distance;
    }
}
