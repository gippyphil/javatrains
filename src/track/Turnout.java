package track;

import java.util.ArrayList;
import java.util.List;

import path.PathException;
import windows.Viewport;

public class Turnout extends Junction {

    // a turnout is basically two (or three?) different tracks
    protected List<BasicTrack> components;
    // some pointers into that list
    protected BasicTrack mainRoute;
    protected BasicTrack divergentRoute; // TODO this doesn't work for triple turnouts?
    protected BasicTrack selectedRoute; // may be null during changes;

    // the fixed end for the entry side of the points
    protected TrackEnd entry;

    public static final double RADIUS_STRAIGHT = 0d;
    public static final double RADIUS_SLOW = 50d;
    public static final double RADIUS_MEDIUM = 100d;
    public static final double RADIUS_FAST = 150d;


    public static Turnout createLeft (TrackEnd end, double radiusDivergent) throws TrackException {
        return Turnout.create(end, Track.Direction.LEFT, RADIUS_STRAIGHT, radiusDivergent);
    }

    public static Turnout createRight (TrackEnd end, double radiusDivergent) throws TrackException {
        return Turnout.create(end, Track.Direction.RIGHT, RADIUS_STRAIGHT, radiusDivergent);
    }

    // TODO doesn't handle Wyes
    public static Turnout create (TrackEnd end, Direction dir, double radiusMain, double radiusDivergent) throws TrackException {
        if (radiusDivergent == RADIUS_STRAIGHT)
            throw new TrackException(null, "Cannot create turnout with a straight divergent leg");
        if (radiusMain != RADIUS_STRAIGHT && radiusDivergent > radiusMain)
            throw new TrackException(null, "Cannot create a curved turnout with a divergent radius greater than the main radius");
            
        Turnout t = new Turnout();

        // TODO - work out arcRadians at a given arcRadius to clear main and divergent by Track.HORZ_CLEARANCE / 2
        // use 12.5 degrees for now
        double clearanceArcRadians = Math.toRadians(12.5);
        t.divergentRoute = CurvedTrack.create(end, dir, radiusDivergent, clearanceArcRadians);
        t.components.add(t.divergentRoute);
        t.ends.add(t.divergentRoute.getEnd(1));
        //t.divergentRoute.getEnd(1).parent = t;
        // disconnect the previous track to avoid errors
        end.connectedEnd = null;
        // TODO - work out how long the main piece needs to be.  use the diagonal distance for now
        double mainLength = Point.findDistance(t.divergentRoute.getEnd(0).getLoc(), t.divergentRoute.getEnd(1).getLoc());
        if (radiusMain == RADIUS_STRAIGHT) {
            t.mainRoute = StraightTrack.create(end, mainLength);
        }
        else {
            t.mainRoute = CurvedTrack.create(end, dir, radiusMain, clearanceArcRadians);
        }
        t.components.add(t.mainRoute);
        t.ends.add(t.mainRoute.getEnd(1));
        //t.mainRoute.getEnd(1).parent = t;
        // disconnect the previous track to avoid errors
        end.connectedEnd = null;




        t.entry = TrackEnd.createAttached(t, end);
        t.ends.add(0, t.entry);

        // TODO: work out the End thing (connecting to straight vs curve)

        //StraightTrack straight = StraightTrack.create(end, length)

        return t;
    }





    protected Turnout () {
        components = new ArrayList<>();
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
    public double getLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public TrackEnd pathFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }





    @Override
    public void render(Viewport v) {
        components.forEach((component) -> component.render(v));        
    }





    @Override
    public PointContext getPointFrom (PointContext previousPoint, TrackEnd start, double distance) throws PathException, TrackException {
        // TODO Auto-generated method stub
        return null;
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
    
}
