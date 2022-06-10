package train;

import animation.AnimationListener;
import path.PathException;
import track.CurvedTrack;
import track.Point;
import track.PointContext;
import track.StraightTrack;
import track.Track;
import track.TrackEnd;
import track.TrackException;
import windows.Viewport;

import java.awt.Color;
import java.util.Arrays;

// TODO is a wagon a subclass?
// TODO loco is definitely a subclass 
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

        this.id = ++nextID;
System.out.format("V%04d: %1.1fm (%1.1f, %1.1f)\n", id, length, distanceToFrontWheel, distanceToBackWheel);
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

        System.out.println("Placing front wheel...");            
        if (previousPoint == null) {
            // if we don't have a previous vehicle we can approximate
            frontWheel = start.getParent().getPointFrom(null, start, distance + Vehicle.GAP / 2 + distanceToFrontWheel);
        } else {
            // if we do have a previous position then we place the front wheel from that
            frontWheel = start.getParent().getPointFrom(previousPoint, start, distance);
        }
        System.out.println("Placing back wheel...");            
        backWheel = frontWheel.getTrack().getPointFrom(frontWheel, frontWheel.getEnd(), wheelbase);

System.out.format("V%04d: FW T%d @ {%1.1f, %1.1f} BW T%d @ {%1.1f, %1.1f}\n", id, frontWheel.getTrack().id, frontWheel.getLat(), frontWheel.getLon(), backWheel.getTrack().id, backWheel.getLat(), backWheel.getLon());

        angle = Point.findAngle(backWheel, frontWheel);

        frontPoint = new Point(frontWheel, angle, distanceToFrontWheel + Vehicle.GAP / 2);
        backPoint = new Point(backWheel, Point.reverse(angle), distanceToBackWheel + Vehicle.GAP / 2);

//System.out.format("V%04d: %s %s == %1.1f\n", id, frontPoint, backPoint, Point.findDistance(frontPoint, backPoint));
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
            viewport.getGraphics().setColor(Color.GRAY);
            viewport.getGraphics().fillPolygon(xPoints, yPoints, 4);
        }
        viewport.getGraphics().setColor(Color.WHITE);
        viewport.getGraphics().drawPolygon(xPoints, yPoints, 4);

        // DEBUG ONLY!
        if (viewport.showDebug()) {
            int x1 = viewport.getX(frontWheel);
            int y1 = viewport.getY(frontWheel);
            int x2 = viewport.getX(backWheel);
            int y2 = viewport.getY(backWheel);

            int gfxWheelbase = viewport.scaledInt(wheelbase);
            viewport.getGraphics().setColor(Color.GREEN);
            viewport.getGraphics().drawArc(x1 - 2, y1 - 2, 4, 4, 0, 360);
            viewport.getGraphics().drawString("T" + frontWheel.getTrack().id, x1, y1 + 10);
            viewport.getGraphics().setColor(Color.RED);
            viewport.getGraphics().drawArc(x2 - 2, y2 - 2, 4, 4, 0, 360);
            viewport.getGraphics().drawString("T" + backWheel.getTrack().id, x2, y2 + 10);
            
            viewport.getGraphics().setColor(Color.YELLOW);
            viewport.getGraphics().drawArc(x1 - gfxWheelbase, y1 - gfxWheelbase, 2 * gfxWheelbase, 2 * gfxWheelbase, 0, 360);

            viewport.getGraphics().setColor(Color.MAGENTA);
            viewport.drawArc(backWheel, wheelbase, 0, Math.PI * 2);

            try {
                for (PointContext wheel : Arrays.asList(frontWheel, backWheel)) {
                    if (!(wheel.getTrack() instanceof CurvedTrack))
                        continue;
                    viewport.setColor(Color.CYAN);
                    viewport.drawLine(wheel, ((CurvedTrack)wheel.getTrack()).pivotPoint);
                    String s = String.format("%1.1f < %1.1f < %1.1f", Math.toDegrees(Point.findAngle(((CurvedTrack)wheel.getTrack()).pivotPoint, wheel.getEnd().getLoc())),
                                                                              Math.toDegrees(Point.findAngle(((CurvedTrack)wheel.getTrack()).pivotPoint, wheel)),
                                                                              Math.toDegrees(Point.findAngle(((CurvedTrack)wheel.getTrack()).pivotPoint, wheel.getTrack().pathFrom(wheel.getEnd()).getLoc())));
                    viewport.setColor(Color.YELLOW);
                    viewport.getGraphics().drawString(s, viewport.getX(wheel) + 10, viewport.getY(wheel));
                }

                // TODO - remove this hack to test line stuff
                StraightTrack st = (StraightTrack)(backWheel.getEnd().getParent().pathFrom(backWheel.getEnd()).getConnectedTrack());
                PointContext pc = st.findIntersection(st.getEnd(0), backWheel, wheelbase, viewport);
System.out.format("Measured length: %1.1f  (wheelbase: %1.1f)\n", Point.findDistance(backWheel, pc), wheelbase);

                viewport.setColor(Color.MAGENTA);
                viewport.drawLine(backWheel, pc);
            } catch (TrackException tx) {
                tx.printStackTrace();
            }
        }

    }
}
