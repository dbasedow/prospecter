package de.danielbasedow.prospecter.core.geo;

/**
 * Represents an area on earth centered at a coordinate and extending distance meters to the north, south, west and
 * east. It is not a circle but more of a square. This means matched results can be up to 40% further away than the
 * specified distance (worst case if match is in one of the corners NW, NE, SE, SW).
 */
public class GeoPerimeter {
    /**
     * Earth's radius in meters
     */
    public static final double EARTH_RADIUS = 6371009.0;
    /**
     * distance between degrees latitude.
     */
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

    /**
     * Get the the northern limit based on center latitude and distance
     *
     * @return northern limit converted to integer
     */
    public int getNorth() {
        return GeoUtil.latitudeToInt(latitude + (distance / LATITUDE_DEGREE_TO_METERS));
    }

    /**
     * Get the the southern limit based on center latitude and distance
     *
     * @return southern limit converted to integer
     */
    public int getSouth() {
        return GeoUtil.latitudeToInt(latitude - (distance / LATITUDE_DEGREE_TO_METERS));
    }

    /**
     * Get the the eastern limit based on center longitude and distance
     *
     * @return eastern limit converted to integer
     */
    public int getEast() {
        return GeoUtil.longitudeToInt(longitude + getLongitudeDegreeOffset());
    }

    /**
     * Get the the western limit based on center longitude and distance
     *
     * @return western limit converted to integer
     */
    public int getWest() {
        return GeoUtil.longitudeToInt(longitude - getLongitudeDegreeOffset());
    }

    /**
     * calculates longitudial offset (in degrees) that represent the distance at the current latitude
     *
     * @return longitudial offset in degrees at the current latitude
     */
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

    /**
     * tests whether the box spans the 180° longitude.
     *
     * @return true if eastern limit lies in western hemisphere and western limit in eastern hemisphere
     */
    public boolean spans180Longitude() {
        if (getEast() > GeoUtil.longitudeToInt(180)) {
            return true;
        } else if (getWest() < GeoUtil.longitudeToInt(-180)) {
            return true;
        }
        return false;
    }

    /**
     * Mirrors the perimeter into fake space. Used to mirror perimeter boxes that span hemisphere at 180 longitude.
     *
     * @return additional perimeter for indexing
     */
    public GeoPerimeter mirrorInFakeSpace() {
        if (longitude > 0) {
            return new GeoPerimeter(latitude, longitude - 360, distance);
        } else {
            return new GeoPerimeter(latitude, longitude + 360, distance);
        }
    }
}
