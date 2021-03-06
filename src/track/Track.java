package track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import path.PathException;
import windows.Viewport;

public abstract class Track {

    public static final double GAUGE = 1.43; // metres .. close enough

    public static final double HORZ_CLEARANCE = 4.0; // metres? between tracks
    
    public enum Direction { LEFT, RIGHT, WYE };

    private static  int nextID = 0; 
    public int id;

    protected Track () {
        id = ++nextID;
    }

    // the external connection points of this track
    protected List<TrackEnd> ends = new ArrayList<>();

    // the internal control points used for curve centres, splines, etc
    private List<Point> referencePoints = new ArrayList<>();

    public TrackEnd getEnd(int index) {
        return ends.get(index);
    }

    public abstract double getLength ();

    public List<TrackEnd> getEnds () {
        return ends;
    }

    protected TrackEnd addEnd (TrackEnd e) {
        ends.add(e);
        addReferencePoint(e);
        return e;
    }

    protected TrackEnd addEndFirst (TrackEnd e) {
        ends.add(0, e);
        addReferencePoint(e);
        return e;
    }

    protected Point addReferencePoint (Point p) {
        if (!referencePoints.contains(p))
            referencePoints.add(p);
        return p;
    }

    protected Stream<Point> referencePointStream () {
        return referencePoints.stream();
    }

    /**
     * Find the other TrackEnd that connects to start
     * @param start The TrackEnd from this Track to start the path
     * @return the other end of the path
     * @throws TrackException if start is not one of the ends in this track
     */
    public abstract TrackEnd pathFrom (TrackEnd start) throws TrackException;

    public abstract void render (Viewport v);

    /**
     * Retuns a point on this track that is distance metres from a pivot point
     * beyond a given end.  If distance exceeds the length of track, it will
     * follow the path to find the point.
     * 
     * @param previousPoint The point to measure from (probably on a previous
     * track.  If null, then the point at the end is used.
     * @param start The end of this track to measure from
     * @param distance The distance to measure along
     * @return The point in space, in context of the track and track end
     * @throws PathException if distance extends beyond the end of the valid path
     * @throws TrackException if start is not one of the ends in this track
     */
    public abstract PointContext getPointFrom (PointContext previousPivot, TrackEnd start, double distance) throws PathException, TrackException;


    /**
     * Move and rotate this track so that sourceEnd on this track aligns and optionally connects with targetEnd.
     * This will also move any other connected tracks to other Ends of this track
     * @param sourceEnd the End on the track to move
     * @param targetEnd the End on a different track to align and connect with
     * @param connectEnds connect the ends logically
     * @throws TrackException if the move is not impossible
     */
    public void moveAndConnect (TrackEnd sourceEnd, TrackEnd targetEnd, boolean connectEnds, Set<TrackEnd> alreadyMoved) throws TrackException {
        if (!ends.contains(sourceEnd))
            throw new TrackException(this, "Doesn't contain " + sourceEnd);
        if (ends.contains(targetEnd))
            throw new TrackException(this, "Can't connect track to itself " + targetEnd);

        // TODO - add some path type checks for connected tracks
        if (alreadyMoved == null)
            alreadyMoved = new HashSet<>();


        // work out all the range and bearings relative to sourceEnd for all other ends and control points
        Map<Point, RangeAndBearing> rangeAndBearingToRefPoints = new HashMap<>();
        referencePoints.stream().distinct().filter(e -> e != sourceEnd).forEach(rp -> {
            rangeAndBearingToRefPoints.put(rp, Point.findRangeAndBearing(sourceEnd, rp));
        });

        // move and rotate the source Point
        double rotationAngle = Point.reverse(sourceEnd.getAng()) - targetEnd.getAng();
System.out.format("Rotating by %1.1f\u00A0\n", Math.toDegrees(rotationAngle));
        sourceEnd.moveAndConnect(targetEnd, connectEnds);
        alreadyMoved.add(sourceEnd);

        // and the other reference points
        // finally adjust all other ends
        for (Map.Entry<Point, RangeAndBearing> entry : rangeAndBearingToRefPoints.entrySet()) {
            if (entry.getKey() == sourceEnd)
                continue;
            if (entry.getKey() instanceof TrackEnd && !alreadyMoved.contains(entry.getKey())) {
                TrackEnd otherEnd = (TrackEnd)entry.getKey();
                otherEnd.moveAndRotate(sourceEnd, entry.getValue(), rotationAngle);
                alreadyMoved.add(otherEnd);
                if (otherEnd.connectedEnd != null) {
                    otherEnd.getConnectedTrack().moveAndConnect(otherEnd.connectedEnd, otherEnd, false, alreadyMoved); // already connected
                }
            } else
                entry.getKey().moveTo(new Point(sourceEnd, Point.subtract(entry.getValue().bearing, rotationAngle), entry.getValue().range));
        }
    }

    public void moveAndConnect (TrackEnd sourceEnd, TrackEnd targetEnd, boolean connectEnds) throws TrackException {
        moveAndConnect(sourceEnd, targetEnd, connectEnds, null);
    }

}
