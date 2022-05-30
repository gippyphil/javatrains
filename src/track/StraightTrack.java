package track;

import java.awt.Color;

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

    @Override
    public PointContext getPointFrom(TrackEnd end, double distance) throws PathException, TrackException {
System.out.println(this + ".getPointFrom(" + distance + ")");
        if (!ends.contains(end))
            throw new TrackException(this, "Doesn't contain " + end);

        // do we need to look at the next piece?
        if (distance > length)
        {
            double remainingLength = distance - length;
            TrackEnd otherEnd = pathFrom(end);
            if (otherEnd.connectedEnd == null)
                throw new PathException(this, String.format("Point is %1.1fm off the end of track", remainingLength));

            return otherEnd.connectedEnd.getParent().getPointFrom(otherEnd.connectedEnd, remainingLength);
        }

        double direction = Point.add(end.getAng(), Math.PI); // 180 deg away
        return new PointContext(end.getLoc(), direction, distance, this, end, distance);
    }
}
