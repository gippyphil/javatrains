package track;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import path.PathException;
import windows.Viewport;

public class Turntable extends Junction {

    protected double length;
    protected Point pivotPoint;

    public static final double ENTRY_LENGTH = 2;
    public static final double CLEARANCE = Track.HORZ_CLEARANCE * 0.666;
    
    protected List<StraightTrack> entryPoints;
    protected StraightTrack table;

    public static Turntable create (TrackEnd end, double length, int exits) throws TrackException {
        int maxExits = (int)Math.floor(Math.PI * length / CLEARANCE);
        if (exits < 1 || exits > maxExits)
            throw new TrackException(String.format("A %1.1fm turntable must have between 1 and %d exits", length, maxExits));


        Turntable t = new Turntable();
        t.length = length;
        StraightTrack entry = StraightTrack.create(end, ENTRY_LENGTH);
        t.pivotPoint = new Point(entry.getEnd(1), entry.getEnd(1).getAng(), length / 2);
        t.entryPoints.add(entry);
        t.table = StraightTrack.create(entry.getEnd(1), length);
        t.ends.add(entry.getEnd(0));
        double exitAngle = t.table.getEnd(1).getAng();
        double exitAngleStep = (Math.PI * 2) / (Math.PI * length / CLEARANCE);
        for (int i = 1; i < exits; i++)
        {
            Point exitStartPoint = new Point(t.pivotPoint, exitAngle, length / 2);
            Point exitFinishPoint = new Point(exitStartPoint, exitAngle, ENTRY_LENGTH);

            if (i == 1)
                entry = StraightTrack.create(t.table.getEnd(1), ENTRY_LENGTH);
            else
                entry = StraightTrack.create(exitStartPoint, exitFinishPoint);
            t.ends.add(entry.getEnd(1));
            t.entryPoints.add(entry);
            exitAngle += ((exitAngleStep * (i)) * ((i % 2 == 1) ? 1.0 : -1.0));
        }

        t.ends.forEach(e -> e.setParent(t));
        t.entryPoints.forEach(ep -> ep.setParent(t));

        return t;
    }

    protected Turntable () {
        super();
        entryPoints = new ArrayList<>();
    }

    @Override
    public void onAnimationTimer() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void selectPath(TrackEnd from, TrackEnd to) throws PathException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isSelected(TrackEnd from, TrackEnd to) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<TrackEnd> pathsFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BasicTrack> tracksFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getLength() {
        // TODO Auto-generated method stub
        return length;
    }

    @Override
    public TrackEnd pathFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void render(Viewport v) {
        v.setColor(Color.DARK_GRAY);
        v.drawArc(pivotPoint, length / 2, 0, 360);
        entryPoints.forEach(ep -> ep.render(v));
        table.render(v);
    }

    @Override
    public PointContext getPointFrom(PointContext previousPivot, TrackEnd start, double distance)
            throws PathException, TrackException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
