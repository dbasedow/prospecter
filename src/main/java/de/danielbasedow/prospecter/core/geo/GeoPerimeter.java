package de.danielbasedow.prospecter.core.geo;

/**
 * Represents an area on earth centered at a coordinate and extending distance meters to the north, south, west and
 * east. It is not a circle but more of a square. This means matched results can be up to 40% further away than the
 * specified distance (worst case if match is in one of the corners NW, NE, SE, SW).
 */
public class GeoPerimeter {

    public static final double EQUATORIAL_RADIUS = 6378137;

    public static final double POLAR_RADIUS = 6356752.3;

    private final double latitude;
    private final double longitude;
    private final int distance;

    private final double north;
    private final double south;
    private final double east;
    private final double west;

    /**
     * @param latitude  latitude of center
     * @param longitude longitude of center
     * @param distance  in meters
     */
    public GeoPerimeter(double latitude, double longitude, int distance) {
        this.latitude = Math.toRadians(latitude);
        this.longitude = Math.toRadians(longitude);
        this.distance = distance;

        double r = getRadiusAtLatitude(this.latitude);
        double pr = r * Math.cos(this.latitude);
        south = Math.toDegrees(this.latitude - distance / r);
        north = Math.toDegrees(this.latitude + distance / r);
        west = Math.toDegrees(this.longitude - distance / pr);
        east = Math.toDegrees(this.longitude + distance / pr);
    }

    /**
     * Get the the northern limit based on center latitude and distance
     *
     * @return northern limit converted to integer
     */
    public int getNorth() {
        return GeoUtil.latitudeToInt(north);
    }

    /**
     * Get the the southern limit based on center latitude and distance
     *
     * @return southern limit converted to integer
     */
    public int getSouth() {
        return GeoUtil.latitudeToInt(south);
    }

    /**
     * Get the the eastern limit based on center longitude and distance
     *
     * @return eastern limit converted to integer
     */
    public int getEast() {
        return GeoUtil.longitudeToInt(east);
    }

    /**
     * Get the the western limit based on center longitude and distance
     *
     * @return western limit converted to integer
     */
    public int getWest() {
        return GeoUtil.longitudeToInt(west);
    }

    public int getDistance() {
        return distance;
    }

    public double getNorthDouble() {
        return north;
    }

    public double getSouthDouble() {
        return south;
    }

    public double getEastDouble() {
        return east;
    }

    public double getWestDouble() {
        return west;
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
            return new GeoPerimeter(Math.toDegrees(latitude), Math.toDegrees(longitude) - 360, distance);
        } else {
            return new GeoPerimeter(Math.toDegrees(latitude), Math.toDegrees(longitude) + 360, distance);
        }
    }

    private double getRadiusAtLatitude(double latitude) {
        double a = EQUATORIAL_RADIUS * EQUATORIAL_RADIUS * Math.cos(latitude);
        double b = POLAR_RADIUS * POLAR_RADIUS * Math.sin(latitude);
        double c = EQUATORIAL_RADIUS * Math.cos(latitude);
        double d = POLAR_RADIUS * Math.sin(latitude);

        return Math.sqrt((a * a + b * b) / (c * c + d * d));
    }
}
