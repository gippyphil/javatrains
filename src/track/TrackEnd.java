package track;

import windows.Viewport;

import java.awt.Color;

public class TrackEnd {

    private static int nextID = 0;

    private Point loc;
    private double ang;
    private Track parent;
    public int id;

    protected TrackEnd connectedEnd;

    public static TrackEnd create (Track parent, Point location, double angle) {
        return new TrackEnd (parent, location, angle);
    }

    public Track getParent() {
        return parent;
    }

    /**
     * The only time a parent would change would be for a junction
     * @param parent
     * @return
     */
    public void setParent(Junction junc) {
        parent = junc;
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
        return String.format("%04d: %s@%1.3f\u00B0", id, getLoc().toString(), Math.toDegrees(getAng()));
    }

    public void render (Viewport v)
    {
        if (v.showDebug()) {
            final int LEN = v.scaledInt(5);
            final int x = v.getX(getLoc());
            final int y = v.getY(getLoc());
    
            double leftAng = Point.add(getAng(), Math.PI / 2);
            double rightAng = Point.subtract(getAng(), Math.PI / 2);

            int x1 = x + (int)Math.round(Math.sin(leftAng) * LEN);
            int y1 = y - (int)Math.round(Math.cos(leftAng) * LEN);

            int x2 = x + (int)Math.round(Math.sin(rightAng) * LEN);
            int y2 = y - (int)Math.round(Math.cos(rightAng) * LEN);

            v.getGraphics().setColor(this.connectedEnd == null ? Color.RED : Color.GREEN);
            v.getGraphics().drawString(String.format("%03d: %1.0f\u00B0 - %s", id, Math.toDegrees(ang), (this.connectedEnd == null ? "" : this.connectedEnd.parent.id)), x2, y2);
            
            v.getGraphics().setColor(Color.DARK_GRAY);
            v.getGraphics().drawLine(x1, y1, x2, y2);
        }
    }

    public TrackEnd getConnectedEnd() {
        return connectedEnd;
    }

    public Track getConnectedTrack() {
        return connectedEnd != null ? connectedEnd.parent : null;
    }

}
