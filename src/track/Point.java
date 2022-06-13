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

    public static boolean inRange (double min, double val, double max) {
        return (min <= val && val <= max) || (max <= val && val <= min);
    }

    public static Point findIntersection (double arcX, double arcY, double radius, double lineX1, double lineY1, double lineX2, double lineY2) {

        double offsetY = lineY1 - arcY;
        double offsetX = lineX1 - arcX;
        double riseOverRun = (lineY2 - lineY1) / (lineX2 - lineX1);
        double x1 = Double.NaN, y1 = Double.NaN, x2 = Double.NaN, y2 = Double.NaN;

        boolean intersection1 = false;
        boolean intersection2 = false;
        if (Double.isFinite(riseOverRun)) {
            double arcZeroYOffset = offsetY - (offsetX * riseOverRun);
            double denominator = 1 + Math.pow(riseOverRun, 2);
            double sqrtPart = Math.sqrt(Math.pow(radius, 2) + Math.pow(riseOverRun, 2) * Math.pow(radius, 2) - Math.pow(arcZeroYOffset, 2));
            if (!Double.isNaN(sqrtPart))
            {
                x1 = ((-arcZeroYOffset * riseOverRun) + sqrtPart) / denominator;
                y1 = arcY + arcZeroYOffset + (x1 * riseOverRun);
                x1 += arcX;
                x2 = -(((arcZeroYOffset * riseOverRun) + sqrtPart) / denominator);
                y2 = arcY + arcZeroYOffset + (x2 * riseOverRun);
                x2 += arcX;
            }
        }
        else if (offsetX != 0.0) {
            // use pythagorus' theorm using radius and X delta from arc.
            x2 = x1 = lineX1; // or lineX2 - they are the same
            y1 = arcY - Math.sqrt(Math.pow(radius, 2) - Math.pow(offsetX, 2));
            y2 = arcY + Math.sqrt(Math.pow(radius, 2) - Math.pow(offsetX, 2));
        }
        else // (just +/- radius)
        {
            x1 = x2 = arcX;
            y1 = arcY + radius;
            y2 = arcY - radius;
        }
            
        intersection1 = Point.inRange(lineX1, x1, lineX2) && Point.inRange(lineY1, y1, lineY2);
        intersection2 = Point.inRange(lineX1, x2, lineX2) && Point.inRange(lineY1, y2, lineY2);
        if (intersection1 && !intersection2)
            return new Point(y1, x1);
        else if (intersection2 && !intersection1)
            return new Point(y2, x2);
        else
            return null;
    }

    /**
     * @param startAngle the start angle
     * @param angle the angle to check 
     * @param endAngle the end angle
     * @return true when start <= angle <= endAngle
     */
    public static boolean containsAngle (double startAngle, double angle, double endAngle)
    {
        if (endAngle > startAngle)
            return (startAngle <= angle && angle <= endAngle);
        else // wraps through 0
            return (startAngle <= angle && angle < 360.0)|| (angle <= endAngle && angle >= 0.0);
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
