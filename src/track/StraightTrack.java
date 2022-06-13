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

    public PointContext findIntersection (TrackEnd end, Point pivotPoint2, double radius2, Viewport v) throws TrackException {

        if (v != null) {
            v.setColor(Color.RED);
            v.drawLine(ends.get(0).getLoc(), ends.get(1).getLoc());
        }

        double xa = pivotPoint2.getLon();
        double ya = pivotPoint2.getLat();

        double x1 = ends.get(0).getLoc().getLon();
        double x2 = ends.get(1).getLoc().getLon();
        double y1 = ends.get(0).getLoc().getLat();
        double y2 = ends.get(1).getLoc().getLat();


        double dx = x2 - x1;
        double dy = y2 - y1;

        double riseOverRun = Math.atan(dy/dx);
        double yOffset = y1 - ya;
        double xOffset = x1 - xa;

        // thanks to https://www.symbolab.com/solver/equation-calculator/x%5E%7B2%7D%20%2B%20%5Cleft(o%20%2B%20x%5Ccdot%20a%5Cright)%5E%7B2%7D%20%3D%20r%5E%7B2%7D?or=input

        double xOpt1 = (-2 * riseOverRun * yOffset) - 2 * Math.sqrt(Math.pow(riseOverRun, 2) * Math.pow(radius2, 2) - Math.pow(yOffset, 2) + Math.pow(radius2, 2)) / (2 * (1 + Math.pow(riseOverRun, 2)));
        double yOpt1 = yOffset + xOpt1 * riseOverRun;

        double xOpt2 = (riseOverRun * yOffset) + Math.sqrt(Math.pow(radius2, 2) + Math.pow(riseOverRun, 2) * Math.pow(radius2, 2) - Math.pow(yOffset, 2)) / Math.pow(riseOverRun, 2) + 1;
        double yOpt2 = yOffset + xOpt2 * riseOverRun;

        if (v != null) {
            v.setColor(Color.GREEN);
            v.drawLine(new Point(yOpt1, xOffset + xOpt1), new Point(yOpt2, xOffset + xOpt2));
        }

        return new PointContext(yOpt1, xOffset + xOpt1, this, end);
    }



    public PointContext findIntersection_DODGYCODEFROMWEB (TrackEnd end, Point pivotPoint2, double radius2, Viewport v) throws TrackException {

        if (v != null) {
            v.setColor(Color.white);
            v.drawLine(ends.get(0).getLoc(), ends.get(1).getLoc());
        }

        double xa = pivotPoint2.getLon();
        double ya = pivotPoint2.getLat();

        double x1 = ends.get(0).getLoc().getLon();
        double x2 = ends.get(1).getLoc().getLon();
        double y1 = ends.get(0).getLoc().getLat();
        double y2 = ends.get(1).getLoc().getLat();


        double dx = x2 - x1;
        double dy = y2 - y1;

        double al = Math.atan(dy/dx);

        double c = (xa - x1 + radius2 * Math.cos(al)) / dx;
        // -- or -- ... these don't give the same result
        //double c = (ya - y1 + radius2 * Math.sin(al)) / dy;

        return new PointContext((y1+c * dy), (x1+c * dx), this, end);
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
        } else if (previousPivot.getTrack() == this || Path.findMostDirectPath(this, previousPivot.getTrack(), false).isStraight()) {
            
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
        } else 
        // TODO handle a string of straight track maybe?
        {
            // coming off a curve possibly? maths/intersections... 
            return null;
        }
    }
}
