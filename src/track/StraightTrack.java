package track;

import java.awt.Color;

import path.Path;
import path.PathException;
import windows.Viewport;

public class StraightTrack extends BasicTrack {
    
    public double length;

    public static StraightTrack create (Point start, Point end) {
        StraightTrack t = new StraightTrack();
        // ensure a number between 0 and 360
        double exitAngle = Point.findAngle(start, end);
        double entryAngle = Point.reverse(exitAngle);

        t.length = Point.findDistance(start, end);

        t.ends.add(TrackEnd.create(t, start, entryAngle));
        t.ends.add(TrackEnd.create(t, end, exitAngle));

        return t;
    }

    public static StraightTrack create (TrackEnd end, double length) throws TrackException {
        StraightTrack t = new StraightTrack();

        t.length = length;
        t.ends.add(TrackEnd.createAttached(t, end));
        t.ends.add(TrackEnd.create(t, new Point(end.getLoc(), end.getAng(), length), end.getAng()));
//        t.ends.add(TrackEnd.create(t, new Point((Math.cos(end.getAng()) * length) + end.getLoc().lat, (Math.sin(end.getAng()) * length) + end.getLoc().lon), end.getAng()));
        t.getEnd(0).connect(end);

        System.out.println(t.ends.get(0) + " -> " + t.ends.get(1));

        return t;
    }

    protected StraightTrack () {
        super();
    }

    @Override
    public double getLength () {
        return length;
    }

    @Override
    public void render (Viewport v) {
        v.getGraphics().setColor(Color.GRAY);
        if (v.showTwoRails()) {
            for (double railOffset = Track.GAUGE / -2; railOffset <= Track.GAUGE / 1.999; railOffset += Track.GAUGE) {
                int x1 = v.getXOffset(ends.get(0), railOffset, true);
                int y1 = v.getYOffset(ends.get(0), railOffset, true);
                int x2 = v.getXOffset(ends.get(1), railOffset, false);
                int y2 = v.getYOffset(ends.get(1), railOffset, false);

                v.getGraphics().drawLine(x1, y1, x2, y2);
            }
        }
        else {
            int x1 = v.getX(ends.get(0).getLoc());
            int y1 = v.getY(ends.get(0).getLoc());
            int x2 = v.getX(ends.get(1).getLoc());
            int y2 = v.getY(ends.get(1).getLoc());
    
            v.getGraphics().drawLine(x1, y1, x2, y2);
        }
        super.render(v);
    }

    public PointContext findIntersection (TrackEnd end, Point pivotPoint2, double radius, Viewport v) throws TrackException {

        if (v != null) {
            v.setColor(Color.RED);
            v.drawLine(ends.get(0).getLoc(), ends.get(1).getLoc());
        }

        double arcX = pivotPoint2.getLon();
        double arcY = pivotPoint2.getLat();

        double lineX1 = ends.get(0).getLoc().getLon();
        double lineX2 = ends.get(1).getLoc().getLon();
        double lineY1 = ends.get(0).getLoc().getLat();
        double lineY2 = ends.get(1).getLoc().getLat();

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
            
                intersection1 = Point.inRange(lineX1, x1, lineX2) && Point.inRange(lineY1, y1, lineY2);
                intersection2 = Point.inRange(lineX1, x2, lineX2) && Point.inRange(lineY1, y2, lineY2);
                if (intersection1 && !intersection2)
                    return new PointContext(y1, x1, this, end);
                else if (intersection2 && !intersection1)
                    return new PointContext(y2, x2, this, end);
            }
        }
        return null;
    }


    @Override
    public PointContext getPointFrom (PointContext previousPivot, TrackEnd end, double distance) throws PathException, TrackException {
System.out.println("S" + id + ".getPointFrom(" + distance + ")");
        if (!ends.contains(end))
            throw new TrackException(this, "Doesn't contain " + end);

        if (previousPivot == null)
        {
            // simple!            
            if (distance > length)
            {
                double remainingLength = distance - length;
                TrackEnd otherEnd = pathFrom(end);
                if (otherEnd.connectedEnd == null)
                    throw new PathException(this, String.format("Point is %1.1fm off the end of track", remainingLength));
    
                return otherEnd.connectedEnd.getParent().getPointFrom(previousPivot, otherEnd.connectedEnd, remainingLength);
            }
    
            double direction = Point.add(end.getAng(), Math.PI); // 180 deg away
            return new PointContext(end.getLoc(), direction, distance, this, end);
        } else if (previousPivot.getTrack() == this) {
            
            // almost as simple!
            double calcLength = Point.findDistance(previousPivot, pathFrom(end).getLoc());
            if (distance > calcLength)
            {
                TrackEnd otherEnd = pathFrom(end);
                if (otherEnd.connectedEnd == null) {
                    double remainingLength = distance - calcLength;
                    throw new PathException(this, String.format("Point is %1.1fm off the end of track", remainingLength));
                }
                return otherEnd.getConnectedTrack().getPointFrom(previousPivot, otherEnd.connectedEnd, distance);
            }

            double direction = Point.add(end.getAng(), Math.PI); // 180 deg away
            return new PointContext(previousPivot, direction, distance, this, end);
        } 
        else {
            // coming off a curve possibly? maths/intersections... 
            return this.findIntersection(end, previousPivot, distance, null);
        }
    }
}
