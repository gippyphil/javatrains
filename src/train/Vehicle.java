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

// TODO special subclass for articulated vehicles (eg; garrats)?
// TODO special subclass for permanently coupled vehicles (eg: engine and tender)?
public class Vehicle implements AnimationListener {

    // TODO: physics stuff - weight, drag, etc
    public static final double WIDTH = 3.0d; // metres ... good enough
    public static final double GAP = 0.66d; // metres between vehicles;

    // wheels are rotation points with the track, so a single axle or the centre point of a set of fixed axles (eg: a bogie)
    // a steam loco would have two 'wheels' for the front and rear driving wheels - the leading and trailing trucks don't affect
    // the vehicles position
    protected PointContext frontWheel;
    protected PointContext backWheel;
    protected Point frontPoint;
    protected Point backPoint;
    protected double distanceToFrontWheel; // metres
    protected double distanceToBackWheel; // metres
    protected double length; // metres
    protected double wheelbase; // metres
    protected double weight; // kilograms

    protected Vehicle frontCoupler;
    protected Vehicle backCoupler;

    protected double angle;
    protected double speed; // metres per second

    protected int id;

    protected static int nextID = 0;

    protected Color col;

    public Vehicle (double length, double distanceToFrontWheel, double distanceToBackWheel, double weight)
    {
        this.length = length;
        this.weight = weight;
        this.distanceToFrontWheel = distanceToFrontWheel;
        this.distanceToBackWheel = distanceToBackWheel;
        this.wheelbase = length - distanceToBackWheel - distanceToFrontWheel;

        this.col = new Color(127 + (int)Math.floor(Math.random() * 128), 127 + (int)Math.floor(Math.random() * 128), 127 + (int)Math.floor(Math.random() * 128));

        this.id = ++nextID;
System.out.format("V%04d: len %1.3fm (offsets: %1.3f, -%1.3f) wb: %1.3fm\n", id, length, distanceToFrontWheel, distanceToBackWheel, wheelbase);
    }

    public Track getFrontTrack () {
        return frontWheel.getTrack();
    }

    /**
     * Retuns the front of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the front of the vehicle
     */
    public Point getFrontPoint () {
        return frontPoint;
    }

    public PointContext getFrontWheel () {
        return frontWheel;
    }

    public Track getBackTrack () {
        return backWheel.getTrack();
    }

    /**
     * Retuns the back of the vehicle, as it is on the track, viewed from above. This
     * is the point that would interface with another vehicle (eg: the coupler)
     * @return Point the location on the track of the back of the vehicle
     */
    public Point getBackPoint () {
        return backPoint;
    }

    public PointContext getBackWheel () {
        return backWheel;
    }

    @Override
    public void onAnimationTimer() {
        // TODO Auto-generated method stub
        
    }

    public void place (PointContext previousPoint, TrackEnd start, double distance) throws PathException, TrackException
    {
        // TODO (reversed vehicles in consists??)

        if (previousPoint == null) {
            System.out.format("Placing front wheel %1.1fm from end\n", distance);            
            // if we don't have a previous vehicle we can approximate
            frontWheel = start.getParent().getPointFrom(null, start, distance + Vehicle.GAP / 2 + distanceToFrontWheel);
        } else {
            System.out.format("Placing front wheel %1.1fm from previous vehicle back wheel @ %s\n", distance, previousPoint);
            // if we do have a previous position then we place the front wheel from that
            frontWheel = start.getParent().getPointFrom(previousPoint, start, distance);
        }
        System.out.println("Placing back wheel...");            
        backWheel = frontWheel.getTrack().getPointFrom(frontWheel, frontWheel.getEnd(), wheelbase);

System.out.format("V%04d: FW T%d @ %s BW T%d @ %s == %1.3fm (%1.3fm)\n", id, frontWheel.getTrack().id, frontWheel, backWheel.getTrack().id, backWheel, Point.findDistance(frontWheel, backWheel), wheelbase);

        angle = Point.findAngle(backWheel, frontWheel);

        frontPoint = new Point(frontWheel, angle, distanceToFrontWheel + Vehicle.GAP / 2);
        backPoint = new Point(backWheel, Point.reverse(angle), distanceToBackWheel + Vehicle.GAP / 2);

System.out.format("V%04d: %s %s == %1.1f\n", id, frontPoint, backPoint, Point.findDistance(frontPoint, backPoint));
    }

    public void render(Viewport viewport)
    {
        Point bodyStart = new Point(frontPoint, angle, -Vehicle.GAP / 2);
        Point bodyEnd = new Point(backPoint, angle, Vehicle.GAP / 2);

        Point corner1 = new Point(bodyStart, Point.add(angle, Math.PI / 2), WIDTH / 2);
        Point corner2 = new Point(bodyStart, Point.add(angle, -Math.PI / 2), WIDTH / 2);
        Point corner3 = new Point(bodyEnd, Point.add(angle, -Math.PI / 2), WIDTH / 2);
        Point corner4 = new Point(bodyEnd, Point.add(angle, Math.PI / 2), WIDTH / 2);

        int xPoints[] = {viewport.getX(corner1), viewport.getX(corner2), viewport.getX(corner3), viewport.getX(corner4)};
        int yPoints[] = {viewport.getY(corner1), viewport.getY(corner2), viewport.getY(corner3), viewport.getY(corner4)};

        if (!viewport.showDebug()) {
            viewport.getGraphics().setColor(this.col);
            viewport.getGraphics().fillPolygon(xPoints, yPoints, 4);
        }
        viewport.getGraphics().setColor(this instanceof Locomotive ? Color.RED : Color.WHITE);
        viewport.getGraphics().drawPolygon(xPoints, yPoints, 4);

        // DEBUG ONLY!
        if (viewport.showDebug() && viewport.isLargeScale()) {
            int x1 = viewport.getX(frontWheel);
            int y1 = viewport.getY(frontWheel);
            int x2 = viewport.getX(backWheel);
            int y2 = viewport.getY(backWheel);

            viewport.getGraphics().setColor(Color.GREEN);
            viewport.getGraphics().drawArc(x1 - 2, y1 - 2, 4, 4, 0, 360);
            viewport.getGraphics().drawString("T" + frontWheel.getTrack().id, x1, y1 + 10);
            viewport.getGraphics().setColor(Color.RED);
            viewport.getGraphics().drawArc(x2 - 2, y2 - 2, 4, 4, 0, 360);
            viewport.getGraphics().drawString("T" + backWheel.getTrack().id, x2, y2 + 10);
            
            //viewport.getGraphics().setColor(Color.YELLOW);
            //viewport.getGraphics().drawArc(x1 - gfxWheelbase, y1 - gfxWheelbase, 2 * gfxWheelbase, 2 * gfxWheelbase, 0, 360);

            //viewport.getGraphics().setColor(Color.MAGENTA);
            //viewport.drawArc(backWheel, wheelbase, 0, Math.PI * 2);
        }

    }
}
