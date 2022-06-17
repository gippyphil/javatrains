package track;

public class RangeAndBearing {
    protected double range;
    protected double bearing;

    protected RangeAndBearing (double range, double bearing) {
        this.range = range;
        this.bearing = bearing;
    }

    public RangeAndBearing addRotation(double rotationAngle) {
        this.bearing = Point.add(this.bearing, rotationAngle);
        return this;
    }
}


