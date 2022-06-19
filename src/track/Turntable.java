package track;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import path.PathException;
import windows.Viewport;

public class Turntable extends Junction {

    protected double length;
    protected Point pivotPoint;

    public static final double ENTRY_LENGTH = 2;
    public static final double CLEARANCE = Track.HORZ_CLEARANCE * 0.666;
    
    protected StraightTrack table;

    protected List<TrackEnd> entranceEnds;
    protected List<TrackEnd> exitEnds;


    public static Turntable create (TrackEnd end, double length, int entries, int exits) throws TrackException {
        int maxExits = (int)Math.floor(Math.PI * length / CLEARANCE);
        if (exits < 0 || entries < 1 || (entries + exits) > maxExits)
            throw new TrackException(String.format("A %1.1fm turntable must have at least 1 entry and at most %d entries and exits", length, maxExits));

        final TrackEnd throwawayEnd;
        if (end == null)
            end = throwawayEnd = new TrackEnd(null, 0, 0, 0);
        else
            throwawayEnd = null;
    

        Turntable t = new Turntable();
        t.length = length;
        StraightTrack entry = StraightTrack.create(end, ENTRY_LENGTH);
        t.pivotPoint = t.addReferencePoint(new Point(entry.getEnd(1), entry.getEnd(1).getAng(), length / 2));
        t.components.add(entry);
        t.table = StraightTrack.create(entry.getEnd(1), length);
        t.components.add(t.table);
        t.entranceEnds.add(t.addEnd(entry.getEnd(0)));

        double exitAngle = t.table.getEnd(0).getAng();
        double exitAngleStep = (Math.PI * 2) / (Math.PI * length / CLEARANCE);
        for (int i = 1; i < entries; i++)
        {
            exitAngle += ((exitAngleStep * (i)) * ((i % 2 == 1) ? 1.0 : -1.0));
            Point exitStartPoint = new Point(t.pivotPoint, exitAngle, length / 2);
            Point exitFinishPoint = new Point(exitStartPoint, exitAngle, ENTRY_LENGTH);

            entry = StraightTrack.create(exitStartPoint, exitFinishPoint);
            t.entranceEnds.add(t.addEnd(entry.getEnd(1)));
            t.components.add(entry);
        }
        exitAngle = t.table.getEnd(1).getAng();
        exitAngleStep = (Math.PI * 2) / (Math.PI * length / CLEARANCE);
        for (int i = 0; i < exits; i++)
        {
            exitAngle += ((exitAngleStep * (i)) * ((i % 2 == 1) ? 1.0 : -1.0));
            Point exitStartPoint = new Point(t.pivotPoint, exitAngle, length / 2);
            Point exitFinishPoint = new Point(exitStartPoint, exitAngle, ENTRY_LENGTH);

            if (i == 0)
                entry = StraightTrack.create(t.table.getEnd(1), ENTRY_LENGTH);
            else
                entry = StraightTrack.create(exitStartPoint, exitFinishPoint);
            t.exitEnds.add(t.addEnd(entry.getEnd(1)));
            t.components.add(entry);
        }

        t.ends.forEach(e -> {
            e.setParent(t);
            if (e.connectedEnd == throwawayEnd)
                e.disconnect();
        });
        t.components.forEach(ep -> ep.setParent(t));

        return t;
    }

    protected Turntable () {
        super();
        entranceEnds = new ArrayList<>();
        exitEnds = new ArrayList<>();
    }

    public List<TrackEnd> getEntranceEnds () {
        return Collections.unmodifiableList(entranceEnds);
    }

    public TrackEnd getEntranceEnd (int i) {
        return entranceEnds.get(i);
    }

    public List<TrackEnd> getExitEnds () {
        return Collections.unmodifiableList(exitEnds);
    }

    public TrackEnd getExitEnd (int i) {
        return exitEnds.get(i);
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
        v.drawArc(pivotPoint, (length / 2) - 0.5, 0, 360);
        v.drawArc(pivotPoint, (length / 2), 0, 360);
        components.forEach(ep -> ep.render(v));
        table.render(v);
    }

    @Override
    public PointContext getPointFrom(PointContext previousPivot, TrackEnd start, double distance)
            throws PathException, TrackException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
