package track;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import path.PathException;
import windows.Viewport;

public class Turnout extends Junction {

    // some pointers into that list
    protected BasicTrack mainRoute;
    protected BasicTrack divergentRoute; // TODO this doesn't work for triple turnouts?
    protected BasicTrack selectedRoute; // may be null during changes;
    protected Track.Direction divergeDirection;
    protected double divergentArcRadians;

    // the fixed end for the entry side of the points
    protected TrackEnd entry;

    public static final double RADIUS_STRAIGHT = Double.POSITIVE_INFINITY;
    public static final double RADIUS_SLOW = 50d;
    public static final double RADIUS_MEDIUM = 100d;
    public static final double RADIUS_FAST = 150d;


    public static Turnout createLeft (TrackEnd end, double radiusDivergent) throws TrackException {
        return Turnout.create(end, Track.Direction.LEFT, RADIUS_STRAIGHT, radiusDivergent);
    }

    public static Turnout createRight (TrackEnd end, double radiusDivergent) throws TrackException {
        return Turnout.create(end, Track.Direction.RIGHT, RADIUS_STRAIGHT, radiusDivergent);
    }

    public static Turnout create (TrackEnd end, Direction dir, double radiusMain, double radiusDivergent) throws TrackException {
        final TrackEnd throwawayEnd;
        if (end == null)
            end = throwawayEnd = new TrackEnd(null, 0, 0, 0);
        else
            throwawayEnd = null;
        if (Double.isInfinite(radiusDivergent))
            throw new TrackException(null, "Cannot create turnout with a straight divergent leg");
        if ((radiusMain != RADIUS_STRAIGHT && dir != Track.Direction.WYE) && radiusDivergent > radiusMain)
            throw new TrackException(null, "Cannot create a curved turnout with a divergent radius greater than the main radius");
        if ((dir == Track.Direction.WYE && radiusMain != radiusDivergent))
            throw new TrackException(null, "Cannot create a wye turnout with a divergent radius different to the main radius");
        Turnout t = new Turnout();
        t.divergeDirection = dir;

        // work out arcRadians at a given arcRadius to clear main and divergent by Track.HORZ_CLEARANCE / 2
        // use 15 degrees by default
        double clearanceArcRadians = Math.toRadians(15);
        double mainLength = Double.NaN; 
        if (Double.isInfinite(radiusMain)) {
            Point p1 = new Point(end, Point.add(end.getAng(), (dir == Direction.RIGHT ? Math.PI / 2 : -Math.PI / 2)), Track.HORZ_CLEARANCE / 2);
            Point p2 = new Point(p1, end.getAng(), 100000); // a long way ...

            Point pArc = new Point(end, Point.add(end.getAng(), (dir == Direction.RIGHT ? Math.PI / 2 : -Math.PI / 2)), radiusDivergent);

            Point p3 = Point.findIntersection(pArc.getLon(), pArc.getLat(), radiusDivergent, p1.getLon(), p1.getLat(), p2.getLon(), p2.getLat());
            if (p3 == null) {
                //throw new TrackException(null, "Failed to determine divergent track arc length.  This shouldn't happen!");
                clearanceArcRadians = Math.toRadians(12.5);
                t.divergentArcRadians = clearanceArcRadians;
                mainLength = 50;
            }
            else {
                // Law of Cosines gives as the angle between A and B
                double A = radiusDivergent;
                double B = radiusDivergent;
                double C = Point.findDistance(p1, p3);

                double CosA = (B*B + A*A - C*C) / (2*B*A);
                clearanceArcRadians = Math.acos(CosA);
                t.divergentArcRadians = clearanceArcRadians;
                mainLength = Point.findDistance(p1, p3);
            }
        }
        else if (dir == Track.Direction.WYE) {
            // main radius is RIGHT
            Point p1 = new Point(end, Point.add(end.getAng(), Math.PI / 2), Track.HORZ_CLEARANCE / 4);
            Point p2 = new Point(p1, end.getAng(), 100000); // a long way ...

            Point pArc = new Point(end, Point.add(end.getAng(), Math.PI / 2), radiusMain);

            Point p3 = Point.findIntersection(pArc.getLon(), pArc.getLat(), radiusMain, p1.getLon(), p1.getLat(), p2.getLon(), p2.getLat());
            if (p3 == null) {
                throw new TrackException(null, "Failed to determine divergent track arc length.  This shouldn't happen!");
            }
            else {
                // Law of Cosines gives as the angle between A and B
                double A = radiusMain;
                double B = radiusMain;
                double C = Point.findDistance(p1, p3);

                double CosA = (B*B + A*A - C*C) / (2*B*A);
                clearanceArcRadians = Math.acos(CosA);
                t.divergentArcRadians = clearanceArcRadians;
            }
        }
        // this needs more work not a linear relationship
        if (dir != Track.Direction.WYE && Double.isFinite(radiusMain)) {
            // at 90 degrees
            double radiusDelta = radiusMain - radiusDivergent;
            if (dir != Track.Direction.WYE && radiusDelta < Track.HORZ_CLEARANCE / 2)
                throw new TrackException(null, "Insufficent clearance between radii: " + radiusMain + ", " + radiusDivergent);
            clearanceArcRadians = (Math.PI / 2) * ((Track.HORZ_CLEARANCE / 2) / radiusDelta);
            clearanceArcRadians = (Math.PI / 2);
        }
        t.divergentRoute = CurvedTrack.create(end, dir == Track.Direction.WYE ? Track.Direction.LEFT : dir, radiusDivergent, clearanceArcRadians);
        t.components.add(t.divergentRoute);
        t.addEnd(t.divergentRoute.getEnd(1));
        //t.divergentRoute.getEnd(1).parent = t;
        // disconnect the previous track to avoid errors
        end.disconnect();
        if (Double.isInfinite(radiusMain)) {
            t.mainRoute = StraightTrack.create(end, mainLength);
        }
        else {
            t.mainRoute = CurvedTrack.create(end, dir == Track.Direction.WYE ? Track.Direction.RIGHT : dir, radiusMain, clearanceArcRadians);
        }
        t.components.add(t.mainRoute);
        t.addEndFirst(t.mainRoute.getEnd(1));
        // set the turnout to a random state
        t.selectedRoute = (Math.random() >= 0.5) ? t.mainRoute : t.divergentRoute;
        //t.mainRoute.getEnd(1).parent = t;
        // disconnect the previous track to avoid errors
        end.disconnect();




        t.entry = TrackEnd.createAttached(t, end);
        t.addEndFirst(t.entry);

        t.ends.forEach(e -> {
            e.setParent(t);
            if (e.connectedEnd == throwawayEnd)
                e.disconnect();
        });
        final TrackEnd finalEnd = end;
        t.components.forEach(component -> {
            component.setParent(t);
            // connect this component back to the track for traffic flowing back into the junction
            if (throwawayEnd == null)
                component.getEnd(0).connectedEnd = finalEnd;
        });

        //StraightTrack straight = StraightTrack.create(end, length)

        return t;
    }





    protected Turnout () {
        components = new ArrayList<>();
    }

    public double getDivergentArcRadians () {
        return divergentArcRadians;
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
            v.drawLine(ends.get(0), new Point(ends.get(0), Point.add(ends.get(0).getAng(), (selectedRoute == mainRoute ^ divergeDirection == Direction.RIGHT) ? -Math.PI / 2 : Math.PI / 2), Track.GAUGE));
            v.drawArc(ends.get(0), Track.GAUGE / 4, 0, Math.PI * 2);
        }

        if (v.showDebug()) {
            /*if (mainRoute instanceof StraightTrack) {
                Point p1 = new Point(mainRoute.getEnd(0).getLoc(), Point.add(mainRoute.getEnd(1).getAng(), (divergeDirection == Direction.RIGHT ? Math.PI / 2 : -Math.PI / 2)), Track.HORZ_CLEARANCE / 2);
                Point p2 = new Point(p1, mainRoute.getEnd(1).getAng(), mainRoute.getLength());
    
                CurvedTrack curve = (CurvedTrack)divergentRoute;
                Point p3 = Point.findIntersection(curve.pivotPoint.getLon(), curve.pivotPoint.getLat(), curve.radius, p1.getLon(), p1.getLat(), p2.getLon(), p2.getLat());
                if (p3 != null)
                    divergentRoute.getEnd(1).render(v);
                v.drawLine(p1, p2);
            }*/
            for (TrackEnd end : ends)
            {
                if (end.connectedEnd == null)
                    end.render(v);
            }
            /*
            Point labelPoint = new Point(ends.get(0).getLoc(), Point.subtract(ends.get(0).getAng(), Math.PI / 2), Track.GAUGE  * 1.5);
            v.getGraphics().setColor(ends.get(0).connectedEnd == null ? Color.RED : Color.GREEN);
            if (v.isLargeScale())
                v.getGraphics().drawString(String.format("%03d", ends.get(0).id), v.getX(labelPoint), v.getY(labelPoint));
            for (BasicTrack comp : components) {
                Point textPoint = new Point(comp.getEnd(0).getLoc(), Point.add(comp.getEnd(0).getAng(), (comp == mainRoute ^ divergeDirection == Direction.RIGHT) ? -Math.PI * 1.25 : Math.PI * 1.25), Track.GAUGE * 2);
                v.setColor(Color.ORANGE);
                if (v.isLargeScale())
                    v.getGraphics().drawString(String.format("%03d", comp.getEnd(0).id), v.getX(textPoint), v.getY(textPoint));

                labelPoint = new Point(comp.getEnd(1).getLoc(), Point.subtract(comp.getEnd(1).getAng(), Math.PI / 2), Track.GAUGE  * 1.5);
                v.getGraphics().setColor(comp.getEnd(1).connectedEnd == null ? Color.RED : Color.GREEN);
                if (v.isLargeScale())
                    v.getGraphics().drawString(String.format("%03d", comp.getEnd(1).id), v.getX(labelPoint), v.getY(labelPoint));
            }
            */
        }
    }

    @Override
    public PointContext getPointFrom (PointContext previousPoint, TrackEnd start, double distance) throws PathException, TrackException {
        if (!ends.contains(start))
            throw new TrackException(String.format("%s does not contain end %s", this, start));
        if (start == ends.get(0) && selectedRoute == null)
            throw new PathException(this, "Turnout does not have a route selected!");
        if (start == ends.get(0))
            return selectedRoute.getPointFrom(previousPoint, selectedRoute.getEnd(0), distance);
        else
            for (BasicTrack component : components)
                if (component.getEnd(1) == start)
                    return component.getPointFrom(previousPoint, start, distance);

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
