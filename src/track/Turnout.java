package track;

import java.awt.Color;
import java.security.spec.EncodedKeySpec;
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
    protected Track.Direction divergeDirection;

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
        t.divergeDirection = dir;

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
        t.ends.add(0, t.mainRoute.getEnd(1));
        // set the turnout to a random state
        t.selectedRoute = (Math.random() >= 0.5) ? t.mainRoute : t.divergentRoute;
        //t.mainRoute.getEnd(1).parent = t;
        // disconnect the previous track to avoid errors
        end.connectedEnd = null;




        t.entry = TrackEnd.createAttached(t, end);
        t.ends.add(0, t.entry);

        for (TrackEnd tEnd : t.ends)
            tEnd.setParent(t);
        
        for (BasicTrack component : t.components)
            component.parent = t;

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
        // TODO make this non-instantaneous
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
        if (!ends.contains(start))
            throw new TrackException(String.format("%s does not contain end %s", this, start));
        
        if (start == ends.get(0)) {
            if (selectedRoute != null)
                return selectedRoute.getEnd(1);
            else
                throw new TrackException(this, "Turnout does not have a route selected!");
        }
        // so we must have one of the component ends
        for (BasicTrack comp : components) {
            if (comp.getEnd(1) == start)
                return ends.get(0);
        }

        throw new TrackException(this, "Something has gone wrong with this turnouts ends and components: no path found from " + start);
    }

    @Override
    public void render(Viewport v) {
        components.forEach((component) -> component.render(v));   
        
        if (v.showDebug() && selectedRoute != null)
        {
            v.setColor(Color.CYAN);
            v.drawLine(ends.get(0).getLoc(), new Point(ends.get(0).getLoc(), Point.add(ends.get(0).getAng(), (selectedRoute == mainRoute ^ divergeDirection == Direction.RIGHT) ? -Math.PI / 2 : Math.PI / 2), Track.GAUGE));
            v.drawArc(ends.get(0).getLoc(), Track.GAUGE / 4, 0, Math.PI * 2);
        }

        if (v.showDebug()) {
            Point labelPoint = new Point(ends.get(0).getLoc(), Point.subtract(ends.get(0).getAng(), Math.PI / 2), Track.GAUGE  * 1.5);
            v.getGraphics().setColor(ends.get(0).connectedEnd == null ? Color.RED : Color.GREEN);
            v.getGraphics().drawString(String.format("%03d", ends.get(0).id), v.getX(labelPoint), v.getY(labelPoint));
            for (BasicTrack comp : components) {
                Point textPoint = new Point(comp.getEnd(0).getLoc(), Point.add(comp.getEnd(0).getAng(), (comp == mainRoute ^ divergeDirection == Direction.RIGHT) ? -Math.PI * 1.25 : Math.PI * 1.25), Track.GAUGE * 2);
                v.setColor(Color.ORANGE);
                v.getGraphics().drawString(String.format("%03d", comp.getEnd(0).id), v.getX(textPoint), v.getY(textPoint));

                labelPoint = new Point(comp.getEnd(1).getLoc(), Point.subtract(comp.getEnd(1).getAng(), Math.PI / 2), Track.GAUGE  * 1.5);
                v.getGraphics().setColor(comp.getEnd(1).connectedEnd == null ? Color.RED : Color.GREEN);
                v.getGraphics().drawString(String.format("%03d", comp.getEnd(1).id), v.getX(labelPoint), v.getY(labelPoint));
            }
        }
    }

    @Override
    public PointContext getPointFrom (PointContext previousPoint, TrackEnd start, double distance) throws PathException, TrackException {
        if (selectedRoute == null)
            throw new TrackException(this, "Turnout does not have a route selected!");
        if (start == ends.get(0))
            return selectedRoute.getPointFrom(previousPoint, selectedRoute.getEnd(0), distance);
        //else

        throw new TrackException(this, "Something has gone wrong with this turnouts ends and components: no path found from " + start);
    }

    @Override
    public List<TrackEnd> pathsFrom(TrackEnd start) throws TrackException {
        if (!ends.contains(start))
            throw new TrackException(String.format("%s does not contain end %s", this, start));
        if (start == ends.get(0))
            return ends.subList(1, ends.size());
        else
            return ends.subList(0, 1);
    }

    @Override
    public List<BasicTrack> tracksFrom(TrackEnd start) throws TrackException {
        if (!ends.contains(start))
            throw new TrackException(String.format("%s does not contain end %s", this, start));
        if (start == ends.get(0))
            return components;
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getEnd(1) == start)
                return components.subList(i, i + 1);
        }

        throw new TrackException(this, "Something has gone wrong with this turnouts ends and components: no path found from " + start);
    }
}
