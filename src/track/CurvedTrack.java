package track;

import windows.Viewport;

import java.awt.Color;

public class CurvedTrack extends BasicTrack {
    
    public enum Direction { LEFT, RIGHT };

    protected Point pivotPoint;
    protected double length;
    protected double radius;
    protected double arcRadians;
    protected Direction dir;

    public static CurvedTrack create (TrackEnd end, Direction dir, double radius, double arcRadians) throws TrackException {
        CurvedTrack t = new CurvedTrack();
        t.ends.add(TrackEnd.createAttached(t, end));

        double angleToCentre = Point.add(end.getAng(), dir == Direction.RIGHT ? Math.toRadians(90) : Math.toRadians(-90));
        t.pivotPoint = new Point((Math.cos(angleToCentre) * radius) + end.getLoc().lat, (Math.sin(angleToCentre) * radius) + end.getLoc().lon);

//System.out.println("Curve PivotPoint: " + t.pivotPoint);        

        if (dir == Direction.RIGHT)
        {
//System.out.format("RH Curve angles: %1.3f > %1.3f\n", Math.toDegrees(Point.subtract(end.ang, 0)), Math.toDegrees(Point.add(end.ang, arcRadians)));

            t.ends.add(TrackEnd.create(t, new Point((Math.cos(Point.add(arcRadians, end.getAng(), -Math.PI / 2)) * radius) + t.pivotPoint.lat, (Math.sin(Point.add(arcRadians, end.getAng(), -Math.PI / 2)) * radius) + t.pivotPoint.lon), Point.add(end.getAng(), arcRadians)));
        }
        else
        {
//System.out.format("LH Curve angles: %1.3f > %1.3f\n", Math.toDegrees(end.ang), Math.toDegrees(Point.subtract(end.ang, arcRadians)));
            t.ends.add(TrackEnd.create(t, new Point((Math.cos(Point.subtract(end.getAng(), arcRadians, -Math.PI / 2)) * radius) + t.pivotPoint.lat, (Math.sin(Point.subtract(end.getAng(), arcRadians, -Math.PI / 2)) * radius) + t.pivotPoint.lon), Point.subtract(end.getAng(), arcRadians)));
        }
        t.length = radius * arcRadians;
        t.radius = radius;
        t.dir = dir;
        t.arcRadians = arcRadians;

        return t;
    }



    @Override
    public double getLength () {
        return length;
    }

    @Override
    public void render (Viewport v) {

        double startAngle = Point.add(ends.get(0).getAng(), Math.PI); // point outwards from the end
        //startAngle = Point.add(startAngle, dir == Direction.RIGHT ? -Math.PI : (Math.PI)); // add 90 degrees to account for Java's orientation for right handed curves, or minus 90 for left
        int gfxStartAngle = (int)Math.round(Math.toDegrees(startAngle));
        if (dir == Direction.LEFT)
            gfxStartAngle *= -1;
        else // right
            gfxStartAngle = 180 - gfxStartAngle;

//v.getGraphics().setColor(dir == Direction.LEFT ? Color.GREEN : Color.RED);
//v.getGraphics().drawRect(x, y, v.scaledInt(radius * 2), v.scaledInt(radius * 2));
//v.getGraphics().drawString(String.format("startAngle: %1.1f   gfxStartAngle: %d\u00B0", Math.toDegrees(startAngle), gfxStartAngle), x, y - 15);
//v.getGraphics().drawArc(x + v.scaledInt(radius) - 10, y + v.scaledInt(radius) - 10, 20, 20, 0, 360);

        v.getGraphics().setColor(Color.GRAY);
            
        int gfxArcDegrees = (int)Math.round(Math.toDegrees(arcRadians));
        if (dir == Direction.RIGHT)
            gfxArcDegrees *= -1;

        if (v.showTwoRails()) {
            int x = v.getXPlus(pivotPoint, -radius, -Track.GAUGE / 2);// - v.scaledInt(radius);
            int y = v.getYPlus(pivotPoint, -radius, -Track.GAUGE / 2);// - v.scaledInt(radius);
//v.getGraphics().setColor(Color.YELLOW);
//v.getGraphics().drawRect(x, y, v.scaledInt(radius * 2 + Track.GAUGE), v.scaledInt(radius * 2 + Track.GAUGE));
            v.getGraphics().drawArc(x, y, v.scaledInt(radius * 2 + Track.GAUGE), v.scaledInt(radius * 2 + Track.GAUGE), gfxStartAngle, gfxArcDegrees);
            x = v.getXPlus(pivotPoint, -radius, Track.GAUGE / 2);// - v.scaledInt(radius);
            y = v.getYPlus(pivotPoint, -radius, Track.GAUGE / 2);// - v.scaledInt(radius);
//v.getGraphics().setColor(Color.BLUE);
//v.getGraphics().drawRect(x, y, v.scaledInt(radius * 2 - Track.GAUGE), v.scaledInt(radius * 2 - Track.GAUGE));
            v.getGraphics().drawArc(x, y, v.scaledInt(radius * 2 - Track.GAUGE), v.scaledInt(radius * 2 - Track.GAUGE), gfxStartAngle, gfxArcDegrees);
        }
        else {
            //int x = v.getX(pivotPoint) - v.scaledInt(radius);
            //int y = v.getY(pivotPoint) - v.scaledInt(radius);
            int x = v.getXPlus(pivotPoint, -radius);
            int y = v.getYPlus(pivotPoint, -radius);
//v.getGraphics().setColor(Color.PINK);
            v.getGraphics().drawArc(x, y, v.scaledInt(radius * 2), v.scaledInt(radius * 2), gfxStartAngle, gfxArcDegrees);
        }


        for (TrackEnd end : ends)
        {
            if (end.connectedEnd == null)
                continue;

            // draw a circle of 12m radius
            double rad2 = 12.0d;

            v.getGraphics().setColor(Color.CYAN);
            v.drawArc(end.getLoc(), rad2, Point.add(end.getAng(), -Math.PI / 2), Math.PI * 2);
            //v.getGraphics().setColor(Color.YELLOW);
            //v.drawArc(pivotPoint, radius, 0, Math.PI * 2);

            v.setColor(Color.ORANGE);
            v.drawLine(pivotPoint, end.getLoc());

            v.setColor(end == ends.get(0) ? Color.GREEN : Color.RED);
            v.drawLine(pivotPoint, findIntersection(end, end.getLoc(), rad2));
        }

        super.render(v);
    }

    public Point findIntersection (TrackEnd end, Point pivotPoint2, double radius2) {
        // three sided triangle where sides:
        //   A = distance between this.pivotPoint and pivotPoint2
        //   B = this.radius
        //   C = radius2
        // Law of Cosines gives as the angle between A and B
        double A = Point.findDistance(pivotPoint, pivotPoint2);
        double B = this.radius;
        double C = radius2;

        double CosA = (B*B + A*A - C*C) / (2*B*A);
        double angle = Math.acos(CosA);

        // measuring from the "other" end has the effect of turning a left turn into a right
        if (dir == Direction.RIGHT ^ end == ends.get(1))
        {
            angle += Point.findAngle(pivotPoint, pivotPoint2);
            return new Point(pivotPoint, angle, radius);
        }
        else
        {
            angle -= Point.findAngle(pivotPoint, pivotPoint2);
            return new Point(pivotPoint, -angle, radius);
        }
    }



    @Override
    public PointContext getPointFrom(TrackEnd end, double distance) {
        // TODO Auto-generated method stub
        return null;
    }
}
