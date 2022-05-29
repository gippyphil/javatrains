package track;

import windows.Viewport;

import java.awt.Color;

public class TrackEnd {

    private static int nextID = 0;

    private Point loc;
    private double ang;
    private Track parent;
    protected int id;

    protected TrackEnd connectedEnd;

    public static TrackEnd create (Track parent, Point location, double angle) {
        return new TrackEnd (parent, location, angle);
    }

    public Track getParent() {
        return parent;
    }

    public Point getLoc() {
        return loc;
    }

    public double getAng() {
        return ang;
    }

    public void setAng(double ang) {
        this.ang = ang;
    }

    public static TrackEnd createAttached (Track parent, TrackEnd other) throws TrackException {
        TrackEnd t = new TrackEnd(parent, other.getLoc().clone(), Point.reverse(other.getAng()));
        t.connect(other);

        return t;
    }

    protected TrackEnd (Track parent, Point location, double angle) {
        this.parent = parent;
        this.loc = location;
        this.setAng(angle);
        this.id = ++nextID;
    }

    public void connect (TrackEnd other) throws TrackException {
        if (this.connectedEnd == null) {
            this.connectedEnd = other;
            other.connect(this);
        }
        else if (this.connectedEnd != other) {
            throw new TrackException(String.format("%s is already connected to %s", this, this.connectedEnd));
        }
    }

    @Override
    public String toString () {
        return String.format("%s@%1.3f\u00B0", getLoc().toString(), Math.toDegrees(getAng()));
    }

    public void render (Viewport v)
    {
        final int LEN = v.scaledInt(2);
        final int x = v.getX(getLoc());
        final int y = v.getY(getLoc());

        double leftAng = Point.add(getAng(), Math.PI / 2);
        double rightAng = Point.subtract(getAng(), Math.PI / 2);

//System.out.format("%03d: Angle: %1.2f\n", id, Math.toDegrees(ang));

        int x1 = x + (int)Math.round(Math.sin(leftAng) * LEN);
        int y1 = y - (int)Math.round(Math.cos(leftAng) * LEN);

        int x2 = x + (int)Math.round(Math.sin(rightAng) * LEN);
        int y2 = y - (int)Math.round(Math.cos(rightAng) * LEN);


        v.getGraphics().setColor(Color.DARK_GRAY);
        v.getGraphics().drawLine(x1, y1, x2, y2);
    }

}
