package track;

import windows.Viewport;

import java.awt.Color;

import path.PathException;

public class CurvedTrack extends BasicTrack {

    public Point pivotPoint;
    protected double length;
    protected double radius;
    protected double arcRadians;
    protected Direction dir;

    public static CurvedTrack create (TrackEnd end, Direction dir, double radius, double arcRadians) throws TrackException {
        CurvedTrack t = new CurvedTrack();
        t.ends.add(TrackEnd.createAttached(t, end));

        double angleToCentre = Point.add(end.getAng(), dir == Direction.RIGHT ? Math.toRadians(90) : Math.toRadians(-90));
        t.pivotPoint = new Point((Math.cos(angleToCentre) * radius) + end.getLoc().lat, (Math.sin(angleToCentre) * radius) + end.getLoc().lon);

//System.out.println("Curve PivotPoint: " + t.pivotPoint);        

        if (dir == Direction.RIGHT)
        {
//System.out.format("RH Curve angles: %1.3f > %1.3f\n", Math.toDegrees(Point.subtract(end.ang, 0)), Math.toDegrees(Point.add(end.ang, arcRadians)));

            t.ends.add(TrackEnd.create(t, new Point((Math.cos(Point.add(arcRadians, end.getAng(), -Math.PI / 2)) * radius) + t.pivotPoint.lat, (Math.sin(Point.add(arcRadians, end.getAng(), -Math.PI / 2)) * radius) + t.pivotPoint.lon), Point.add(end.getAng(), arcRadians)));
        }
        else
        {
//System.out.format("LH Curve angles: %1.3f > %1.3f\n", Math.toDegrees(end.ang), Math.toDegrees(Point.subtract(end.ang, arcRadians)));
            t.ends.add(TrackEnd.create(t, new Point((Math.cos(Point.subtract(end.getAng(), arcRadians, -Math.PI / 2)) * radius) + t.pivotPoint.lat, (Math.sin(Point.subtract(end.getAng(), arcRadians, -Math.PI / 2)) * radius) + t.pivotPoint.lon), Point.subtract(end.getAng(), arcRadians)));
        }
        t.length = radius * arcRadians;
        t.radius = radius;
        t.dir = dir;
        t.arcRadians = arcRadians;

        return t;
    }



    @Override
    public double getLength () {
        return length;
    }

    @Override
    public void render (Viewport v) {

        double startAngle = Point.add(ends.get(0).getAng(), Math.PI); // point outwards from the end
        //startAngle = Point.add(startAngle, dir == Direction.RIGHT ? -Math.PI : (Math.PI)); // add 90 degrees to account for Java's orientation for right handed curves, or minus 90 for left
        int gfxStartAngle = (int)Math.floor(Math.toDegrees(startAngle));
        if (dir == Direction.LEFT)
            gfxStartAngle *= -1;
        else // right
            gfxStartAngle = 180 - gfxStartAngle;

        v.getGraphics().setColor(Color.GRAY);
            
        int gfxArcDegrees = (int)Math.ceil(Math.toDegrees(arcRadians));
        if (dir == Direction.RIGHT)
            gfxArcDegrees *= -1;

        if (v.showTwoRails()) {
            int x = v.getXPlus(pivotPoint, -radius, -Track.GAUGE / 2);// - v.scaledInt(radius);
            int y = v.getYPlus(pivotPoint, -radius, -Track.GAUGE / 2);// - v.scaledInt(radius);
            v.getGraphics().drawArc(x, y, v.scaledInt(radius * 2 + Track.GAUGE), v.scaledInt(radius * 2 + Track.GAUGE), gfxStartAngle, gfxArcDegrees);

            x = v.getXPlus(pivotPoint, -radius, Track.GAUGE / 2);// - v.scaledInt(radius);
            y = v.getYPlus(pivotPoint, -radius, Track.GAUGE / 2);// - v.scaledInt(radius);
            v.getGraphics().drawArc(x, y, v.scaledInt(radius * 2 - Track.GAUGE), v.scaledInt(radius * 2 - Track.GAUGE), gfxStartAngle, gfxArcDegrees);
        }
        else {
            int x = v.getXPlus(pivotPoint, -radius);
            int y = v.getYPlus(pivotPoint, -radius);

            v.getGraphics().drawArc(x, y, v.scaledInt(radius * 2), v.scaledInt(radius * 2), gfxStartAngle, gfxArcDegrees);
        }


        if (v.showDebug() && false) {
            try
            {
                for (TrackEnd end : ends)
                {
                    // draw a circle of 12m radius
                    double rad2 = 12.0d;

                    v.getGraphics().setColor(Color.CYAN);
                    v.drawArc(end.getLoc(), rad2, Point.add(end.getAng(), -Math.PI / 2), Math.PI * 2);
                    //v.getGraphics().setColor(Color.YELLOW);
                    //v.drawArc(pivotPoint, radius, 0, Math.PI * 2);

                    v.setColor(Color.ORANGE);
                    v.drawLine(pivotPoint, end.getLoc());

                    Point intersection = findIntersection(end, end.getLoc(), rad2);
                    if (intersection != null)
                    {
                        v.setColor(end == ends.get(0) ? Color.GREEN : Color.RED);
                        v.drawLine(pivotPoint, intersection);
                    }
                }
            } catch (TrackException ex) {
                ex.printStackTrace();
            }
        }
        super.render(v);
    }

    public PointContext findIntersection (TrackEnd end, Point pivotPoint2, double radius2) throws TrackException {
        // three sided triangle where sides:
        //   A = distance between this.pivotPoint and pivotPoint2
        //   B = this.radius
        //   C = radius2
        // Law of Cosines gives as the angle between A and B
        double A = Point.findDistance(pivotPoint, pivotPoint2);
        double B = this.radius;
        double C = radius2;

        double CosA = (B*B + A*A - C*C) / (2*B*A);
        double angle = Math.acos(CosA);

        // this is impossible
        if (Double.isNaN(angle))
        {
            System.out.format("Impossible triangle %1.1f, %1.1f, %1.1f\n", A, B, C);
            return null;
        }

        double resultAngle = Point.findAngle(pivotPoint, pivotPoint2);
        /*
        if (Math.abs(angle) > Math.abs(arcRadians)) {
            System.out.format("findIntersection([%1.1f, %1.1f], [%1.1f, %1.1f], %1.1f) == %1.1f > %1.1f\n", pivotPoint.getLat(), pivotPoint.getLon(), pivotPoint2.getLat(), pivotPoint2.getLon(), radius2, Math.abs(Math.toDegrees(angle)), Math.abs(Math.toDegrees(arcRadians)));
            return null;
        }*/
//        System.out.format("findIntersection([%1.1f, %1.1f], [%1.1f, %1.1f], %1.1f) == %1.1f <= %1.1f\n", pivotPoint.getLat(), pivotPoint.getLon(), pivotPoint2.getLat(), pivotPoint2.getLon(), radius2, Math.abs(Math.toDegrees(angle)), Math.abs(Math.toDegrees(arcRadians)));

        // measuring from the "other" end has the effect of turning a left turn into a right
        double startAngle = Point.findAngle(pivotPoint, end.getLoc());
        double endAngle = Point.findAngle(pivotPoint, pathFrom(end).getLoc());
        PointContext result = null;
        if (dir == Direction.RIGHT ^ end == ends.get(1)) {
            angle = Point.add(angle, resultAngle);
            result = new PointContext(pivotPoint, angle, radius, this, end);
            if (Point.containsAngle(startAngle, angle, endAngle)/*startAngle <= angle && angle <= endAngle*/) {
System.out.format("R T%d: %1.1f <= %1.1f <= %1.1f   TRUE\n", id, Math.toDegrees(startAngle), Math.toDegrees(angle), Math.toDegrees(endAngle));        
                return result;
            } else {
System.out.format("R T%d: %1.1f <= %1.1f <= %1.1f   FALSE\n", id, Math.toDegrees(startAngle), Math.toDegrees(angle), Math.toDegrees(endAngle));        
                return null;
            }
        }
        else {
            angle -= resultAngle;
            result = new PointContext(pivotPoint, -angle, radius, this, end);
            if (Point.containsAngle(endAngle, Point.subtract(-angle, 0), startAngle)) {
            //if (startAngle >= Point.subtract(-angle, 0) && Point.subtract(-angle, 0) >= endAngle) {
System.out.format("L T%d: %1.1f >= %1.1f >= %1.1f   TRUE\n", id, Math.toDegrees(startAngle), Math.toDegrees(Point.subtract(-angle, 0)), Math.toDegrees(endAngle));        
                return result;
            } else {
System.out.format("L T%d: %1.1f >= %1.1f >= %1.1f   FALSE\n", id, Math.toDegrees(startAngle), Math.toDegrees(Point.subtract(-angle, 0)), Math.toDegrees(endAngle));        
                return null;
            }
        }
    }



    @Override
    public PointContext getPointFrom (PointContext previousPivot, TrackEnd end, double distance) throws PathException, TrackException {
System.out.println("C" + id + ".getPointFrom(" + distance + ")");

        if (!ends.contains(end))
            throw new TrackException(this, "Doesn't contain " + end);

        // if we don't have a previous point, we need to find one distance from the end
        if (previousPivot == null)
        {
System.out.println("Initial placement on T" + this.id);            
            previousPivot = new PointContext(end.getLoc().getLat(), end.getLoc().getLon(), this, end);
        }
        
        PointContext pc = findIntersection(end, previousPivot, distance);
        if (pc != null) {
            return pc;
        // not on this track ... maybe the next one?
        } else if (pathFrom(end).getConnectedTrack() != null) {
System.out.println("Failed to find a point on T" + this.id + ", trying T" + pathFrom(end).getConnectedTrack().id);            
            return pathFrom(end).getConnectedTrack().getPointFrom(previousPivot, pathFrom(end).connectedEnd, distance);
        }
        else {
            throw new PathException(this, "Vehicle does not fit before of track reached");
        }
    }
}
