package track;

public class Point {

    protected double lat;
    protected double lon;
    protected double alt;

    public Point (double lat, double lon, double alt) {
        this.lat = lat;
        this.lon = lon;
        this.alt = alt;
    }
    
    public Point (double lat, double lon)
    {
        this(lat, lon, 0.0d);
    }

    public Point (Point origin, double angle, double distance)
    {
        lat = Math.cos(angle) * distance + origin.lat;
        lon = Math.sin(angle) * distance + origin.lon;
        alt = origin.alt;
    }

    public static double findAngle (Point a, Point b)
    {
        return subtract(Math.atan2(b.lon - a.lon, b.lat - a.lat), 0);
    }

    public static double findDistance (Point start, Point end) {
        return Math.sqrt(Math.pow(end.lat - start.lat, 2) + Math.pow(end.lon - start.lon, 2));
    }

    @Override
    public String toString () {
        return String.format("[%1.3f, %1.3f]", lat, lon);
    }

    @Override
    public Point clone () {
        return new Point(this.lat, this.lon, this.alt);
    }

    public static double reverse (double angle) {
        return add(angle, Math.PI);
    }

    public static double add (double... angles) {
        double result = angles[0];
        for (int i = 1; i < angles.length; i++)
            result += angles[i];
        return result % (2 * Math.PI);
    }

    public static double subtract (double... angles) {
        double result = angles[0];
        for (int i = 1; i < angles.length; i++)
            result = ((result - angles[i]) + 2 * Math.PI) % (2 * Math.PI);
        return result;
    }

    public double getLon () {
        return lon;
    }

    public double getLat () {
        return lat;
    }

    public double getAlt () {
        return alt;
    }
}
