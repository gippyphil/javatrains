package track;

import java.util.ArrayList;
import java.util.List;

import path.PathException;
import windows.Viewport;

public abstract class Track {

    public static final double GAUGE = 1.43; // metres .. close enough

    public static final double HORZ_CLEARANCE = 4.0; // metres? between tracks

    protected List<TrackEnd> ends = new ArrayList<>();

    public TrackEnd getEnd(int index) {
        return ends.get(index);
    }

    public abstract double getLength ();

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

}
