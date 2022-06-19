package track;

import java.util.List;

import java.util.ArrayList;

import java.awt.Color;

import path.PathException;
import windows.Viewport;

public class SplineTrack extends BasicTrack {
    
    protected double length;
    protected List<Point> splinePoints;

    protected List<Point> splinePointsRail1;
    protected List<Point> splinePointsRail2;

    
    protected Point controlPoints[];

    public static SplineTrack create (TrackEnd start, TrackEnd end) throws TrackException {
        SplineTrack spline = new SplineTrack();
        spline.ends.add(TrackEnd.createAttached(spline, start));
        spline.ends.add(TrackEnd.createAttached(spline, end));
        double straightLineDistance = Point.findDistance(start, end);

        spline.controlPoints[0] = new Point(spline.getEnd(0), spline.getEnd(0).getAng(), straightLineDistance * 2.5);
        spline.controlPoints[1] = spline.getEnd(0).clone();
        spline.controlPoints[2] = spline.getEnd(1).clone();
        spline.controlPoints[3] = new Point(spline.getEnd(1), spline.getEnd(1).getAng(), straightLineDistance * 2.5);

        // two points per metre?
        int pCount = (int)Math.floor(straightLineDistance * 1);
        double tStep = 1.0 / pCount;
        
        spline.length = 0.0;
        Point prevP = spline.controlPoints[1];
        for (double t = 0.0; t <= 1.0 - tStep; t += tStep) {
            double tt = Math.pow(t, 2);
            double ttt = Math.pow(t, 3);

            double q1 = -ttt + 2.0 * tt - t;
            double q2 = 3.0 * ttt - 5.0 * tt + 2.0;
            double q3 = -3.0 * ttt + 4.0 * tt + t;
            double q4 = ttt - tt;

            double p1x = spline.controlPoints[0].getLon();
            double p2x = spline.controlPoints[1].getLon();
            double p3x = spline.controlPoints[2].getLon();
            double p4x = spline.controlPoints[3].getLon();
            double p1y = spline.controlPoints[0].getLat();
            double p2y = spline.controlPoints[1].getLat();
            double p3y = spline.controlPoints[2].getLat();
            double p4y = spline.controlPoints[3].getLat();

            double tx = (p1x * q1 + p2x * q2 + p3x * q3 + p4x * q4) / 2.0;
            double ty = (p1y * q1 + p2y * q2 + p3y * q3 + p4y * q4) / 2.0;

            Point p = new Point(ty, tx);
            double angle = Point.findAngle(prevP, p);
            spline.length += Point.findDistance(prevP, p);
//System.out.format("   %1.3f", angle); System.out.println();
            spline.splinePoints.add(p);
            spline.splinePointsRail1.add(new Point(p, angle + Math.PI * 0.5, GAUGE / 2));
            spline.splinePointsRail2.add(new Point(p, angle - Math.PI * 0.5, GAUGE / 2));

            prevP = p;
        }
        spline.length += Point.findDistance(prevP, spline.controlPoints[2]);

        return spline;
    }

    protected SplineTrack () {
        super();
        controlPoints = new Point[4];
        splinePoints = new ArrayList<>();
        splinePointsRail1 = new ArrayList<>();
        splinePointsRail2 = new ArrayList<>();
    }

    @Override
    public double getLength() {
        return length;
    }

    @Override
    public PointContext getPointFrom(PointContext previousPivot, TrackEnd start, double distance) throws PathException, TrackException {
        // iterate through from the end, and check each segment to see if it intersects with the arc of distance radius
        int startIndex = (start == getEnd(0)) ? 0 : splinePoints.size() - 1;
        Point prevPoint = start;
        if (previousPivot == null) {
//System.out.println(String.format("Sp" + id + ": " + previousPivot + " --> %1.1f", distance));
            previousPivot = new PointContext(start, this, start);
        }
        else if (previousPivot.getTrack() == this && previousPivot.getSplineIndex() >= 0) {
//System.out.println(String.format("Sp" + id + ": " + previousPivot + "[%d] --> %1.1f", previousPivot.getSplineIndex(), distance));
            startIndex = previousPivot.getSplineIndex();
            if (startIndex > 0)
            prevPoint = splinePoints.get(startIndex - 1);
        }
        int step =  (start == getEnd(0)) ? 1 : -1;
        for (int i = startIndex; i >= 0 && i < splinePoints.size(); i += step) {
            Point point = splinePoints.get(i);
            double x1 = prevPoint.getLon();
            double y1 = prevPoint.getLat();
            double x2 = point.getLon();
            double y2 = point.getLat();

            Point result = Point.findIntersection(previousPivot.getLon(), previousPivot.getLat(), distance, x1, y1, x2, y2);
            if (result != null) {
//System.out.format("Found spline intersection %s at index: %d %s->%s\n", result, i, prevPoint, point);
                return new PointContext(result, this, start, i);
            }

            prevPoint = point;
        }
        // need to check the last point
        TrackEnd finish = pathFrom(start);
        double x1 = prevPoint.getLon();
        double y1 = prevPoint.getLat();
        double x2 = finish.getLon();
        double y2 = finish.getLat();

        Point result = Point.findIntersection(previousPivot.getLon(), previousPivot.getLat(), distance, x1, y1, x2, y2);
        if (result != null)
            return new PointContext(result, this, start);

        // if we get to the end
        if (finish.getConnectedTrack() != null)
            return finish.getConnectedTrack().getPointFrom(previousPivot, finish.getConnectedEnd(), distance);
        else
            throw new PathException(this, "Point is off the end of track");
    }


    protected int findStartSplineIndex(PointContext previousPivot) {
        // todo - should be a binary search for performance

        int startIndex = (previousPivot.getEnd() == getEnd(0)) ? 0 : splinePoints.size() - 1;
        int step =  (previousPivot.getEnd() == getEnd(0)) ? 1 : -1;
        double minDistance = Point.findDistance(previousPivot, previousPivot.getEnd());
        int lastI = startIndex;
        for (int i = startIndex; i >= 0 && i < splinePoints.size(); i += step) {
            Point point = splinePoints.get(i);
            double thisDistance = Point.findDistance(previousPivot, point);
//System.out.println(String.format("%02d: %1.1f < %1.1f ? ", i, thisDistance, minDistance));            
            // getting further away!
            if (thisDistance > minDistance)
                return lastI;
            minDistance = thisDistance;
            lastI = i;
        }
        return lastI;
    }

    @Override
    public void render (Viewport v) {
        /*
        v.getGraphics().setColor(Color.cyan);
        Arrays.asList(controlPoints).forEach(p -> v.drawArc(p, GAUGE / 2, 0, 360));
        v.getGraphics().setColor(Color.orange);
        splinePoints.forEach(p -> v.drawArc(p, GAUGE / 2, 0, 360));
        */
        v.setColor(Color.GRAY);
        if (v.showTwoRails()) {
            // add 270 degrees because end is pointing away from the spline
            Point lastRail1 = new Point(getEnd(0), getEnd(0).getAng() + Math.PI * 1.5, GAUGE / 2);
            Point lastRail2 = new Point(getEnd(0), getEnd(0).getAng() - Math.PI * 1.5, GAUGE / 2);
            int step = v.isLargeScale() ? 1 : 4;
//System.out.println("Rendering " + splinePoints.size() / step);   
            for (int i = 0; i < splinePoints.size(); i += step) {
//if (i % 2 == 0)
                v.drawLine(lastRail1, lastRail1 = splinePointsRail1.get(i));
//lastRail1 = splinePointsRail1.get(i);
//if (i % 2 == 1)
                v.drawLine(lastRail2, lastRail2 = splinePointsRail2.get(i));
//lastRail2 = splinePointsRail2.get(i);
            }
            v.drawLine(lastRail1, new Point(getEnd(1), getEnd(1).getAng() + Math.PI * 0.5, GAUGE / 2));
            v.drawLine(lastRail2, new Point(getEnd(1), getEnd(1).getAng() - Math.PI * 0.5, GAUGE / 2));
        }
        else {
            v.drawLine(getEnd(0), splinePoints.get(0));
//System.out.println("Rendering " + splinePoints.size() / 4);   
            Point lastPoint = getEnd(0);
            for (int i = 0; i < splinePoints.size(); i += 4) {
                
                v.drawLine(lastPoint, splinePoints.get(i));
                lastPoint = splinePoints.get(i);
            }
            v.drawLine(lastPoint, getEnd(1));
        }
        super.render(v);
    }

}
