package train;

import animation.AnimationListener;
import path.PathException;
import track.Point;
import track.PointContext;
import track.Track;
import track.TrackEnd;
import track.TrackException;
import windows.Viewport;

import java.awt.Color;

// TODO is a wagon a subclass?
// TODO loco is definitely a subclass 
// TODO special subclass for articulated vehicles (eg; garrats)?
// TODO special subclass for permanently coupled vehicles (eg: engine and tender)?
public class Vehicle implements AnimationListener {

    // TODO: physics stuff - weight, drag, etc
    public static final double WIDTH = 3.0d; // metres ... good enough
    public static final double GAP = 1.0d; // metres between vehicles;

    // wheels are rotation points with the track, so a single axle or the centre point of a set of fixed axles (eg: a bogie)
    // a steam loco would have two 'wheels' for the front and rear driving wheels - the leading and trailing trucks don't affect
    // the vehicles position
    protected Wheel frontWheel;
    protected Wheel backWheel;
    protected PointContext frontPoint;
    protected PointContext backPoint;
    protected double distanceToFrontWheel;
    protected double distanceToBackWheel;
    protected double length;
    protected double wheelbase;

    protected double angle;
    protected double speed; // metres per second

    protected int id;

    protected static int nextID = 0;

    protected Vehicle (double length, double distanceToFrontWheel, double distanceToBackWheel)
    {
        this.length = length;
        this.distanceToFrontWheel = distanceToFrontWheel;
        this.distanceToBackWheel = distanceToBackWheel;
        this.wheelbase = length - distanceToBackWheel - distanceToFrontWheel;
        this.frontWheel = new Wheel(this);
        this.backWheel = new Wheel(this);

        this.id = ++nextID;
System.out.format("V%04dd: %1.1fm (%1.1f, %1.1f)\n", id, length, distanceToFrontWheel, distanceToBackWheel);
    }

    public Track getFrontTrack () {
        return frontWheel.track;
    }

    /**
     * Retuns the front of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the front of the vehicle
     */
    public PointContext getFrontPoint () {
        return frontPoint;
    }

    public Track getBackTrack () {
        return backWheel.track;
    }

    /**
     * Retuns the back of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the back of the vehicle
     */
    public PointContext getBackPoint () {
        return backPoint;
    }

    @Override
    public void onAnimationTimer() {
        // TODO Auto-generated method stub
        
    }

    public void place (TrackEnd start, double distance) throws PathException, TrackException
    {
        // TODO this is inaccurate  for curves - need an "intersection" based model on wheelbase
        frontPoint = start.getParent().getPointFrom(start, distance);

        // TODO this is inaccurate  for curves - need an "intersection" based model on wheelbase
        PointContext frontWheelPC = start.getParent().getPointFrom(start, distance + Vehicle.GAP / 2 + distanceToFrontWheel);
        // TODO (reversed vehicles in consists??)
        frontWheel.location = frontWheelPC;
        frontWheel.track = frontWheelPC.getTrack();
        
        // TODO this is inaccurate  for curves - need an "intersection" based model on wheelbase
        PointContext backWheelPC = start.getParent().getPointFrom(start, distance + Vehicle.GAP / 2 + distanceToFrontWheel + wheelbase);
        backWheel.location = backWheelPC;
        backWheel.track = backWheelPC.getTrack();

        angle = Point.findAngle(frontWheel.location, backWheel.location);

        // TODO this is inaccurate  for curves - need an "intersection" based model on wheelbase
        backPoint = start.getParent().getPointFrom(start, distance + length + Vehicle.GAP);
    }

    public void render(Viewport viewport)
    {
        Point bodyStart = new Point(frontPoint, angle, Vehicle.GAP / 2);
        Point bodyEnd = new Point(backPoint, angle, -Vehicle.GAP / 2);

        Point corner1 = new Point(bodyStart, Point.add(angle, Math.PI / 2), WIDTH / 2);
        Point corner2 = new Point(bodyStart, Point.add(angle, -Math.PI / 2), WIDTH / 2);
        Point corner3 = new Point(bodyEnd, Point.add(angle, -Math.PI / 2), WIDTH / 2);
        Point corner4 = new Point(bodyEnd, Point.add(angle, Math.PI / 2), WIDTH / 2);

        int xPoints[] = {viewport.getX(corner1), viewport.getX(corner2), viewport.getX(corner3), viewport.getX(corner4)};
        int yPoints[] = {viewport.getY(corner1), viewport.getY(corner2), viewport.getY(corner3), viewport.getY(corner4)};


        viewport.getGraphics().setColor(Color.GRAY);
        viewport.getGraphics().fillPolygon(xPoints, yPoints, 4);
        viewport.getGraphics().setColor(Color.WHITE);
        viewport.getGraphics().drawPolygon(xPoints, yPoints, 4);

        // DEBUG ONLY!
        if (viewport.showDebug()) {
            int x1 = viewport.getX(frontWheel.location);
            int y1 = viewport.getY(frontWheel.location);
            int x2 = viewport.getX(backWheel.location);
            int y2 = viewport.getY(backWheel.location);

            int gfxWheelbase = viewport.scaledInt(wheelbase);
            viewport.getGraphics().setColor(Color.GREEN);
            viewport.getGraphics().drawArc(x1 - 2, y1 - 2, 4, 4, 0, 360);
            viewport.getGraphics().setColor(Color.RED);
            viewport.getGraphics().drawArc(x2 - 2, y2 - 2, 4, 4, 0, 360);
            
            viewport.getGraphics().setColor(Color.YELLOW);
            viewport.getGraphics().drawArc(x1 - gfxWheelbase, y1 - gfxWheelbase, 2 * gfxWheelbase, 2 * gfxWheelbase, 0, 360);
        }
    }
}
