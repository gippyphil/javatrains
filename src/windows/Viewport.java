package windows;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseEvent;


import java.awt.Graphics;
import track.Point;
import track.TrackEnd;

public class Viewport {
    protected double scale = 20.0d;
    protected double leftLon = 0;
    protected double bottomLat = 0;
    protected int height;
    protected int width;
    protected boolean showDebug;

    protected Graphics2D gfx;

    public Viewport (int width, int height, boolean showDebug) {
        this.width = width;
        this.height = height;
        this.showDebug = showDebug;
        this.leftLon = -(width / 2) / scale;
        this.bottomLat = -(height / 2) / scale;
    }

    public int getX (Point p) {
        double lon = p.getLon() - leftLon;
        return (int)Math.round(scaled(lon));
    }

    public int getXPlus (Point p, double... distances) {
        double lon = p.getLon() - leftLon;
        for (double distance : distances)
            lon += distance;
        return (int)Math.round(scaled(lon));
    }
    
    public int getXOffset (TrackEnd end, double distance, boolean flipAngle) {
        double lon = end.getLoc().getLon() - leftLon;

        double leftAng = Point.add(end.getAng(), Math.PI / (flipAngle ? 2 : -2));

        lon += Math.sin(leftAng) * distance;
        //int y1 = y - (int)Math.floor(Math.cos(leftAng) * distance);

        return (int)Math.round(scaled(lon));
    }

    
    public int getY (Point p) {
        double lat = p.getLat() - bottomLat;
        return height - (int)Math.round(scaled(lat));
    }

    public int getYPlus (Point p, double... distances) {
        double lat = p.getLat() - bottomLat;
        for (double distance : distances)
            lat -= distance;
        return height - (int)Math.round(scaled(lat));
    }
 
    public int getYOffset (TrackEnd end, double distance, boolean flipAngle) {
        double lat = end.getLoc().getLat() - bottomLat;

        double leftAng = Point.add(end.getAng(), Math.PI / (flipAngle ? 2 : -2));

        lat += Math.cos(leftAng) * distance;
        //int y1 = y - (int)Math.floor(Math.cos(leftAng) * distance);

        return height - (int)Math.round(scaled(lat));
    }


    public double scaled (double dist) {
        return dist * scale;
    }

    public int scaledInt (double dist) {
        return (int)Math.round(scaled(dist));
    }

    public Graphics2D getGraphics () {
        return gfx;
    }

    public void setGraphics (Graphics gfx1) {
        this.gfx = (Graphics2D)gfx1;
    }
    
    public boolean showTwoRails ()
    {
        return scaledInt(1.5) >= 4; // if we have less than 2 pixels between the rails, only show one
    }


    public void zoom(MouseWheelEvent mouseWheelEvent) {
        double mouseLat = getLat(mouseWheelEvent.getY());
        double mouseLon = getLon(mouseWheelEvent.getX());
    
        if (mouseWheelEvent.getWheelRotation() < 0)
            scale *= 1.3;
        else
            scale *= (1.0 / 1.3);
        if (scale < 0.2)
            scale = 0.2d;
        else if (scale > 20)
            scale = 20.0d;

        leftLon = mouseLon - ((width / 2) / scale);
        bottomLat = mouseLat - ((height / 2) / scale);
System.out.format("New scale is %1.2f\n", scale);
    }

    public void center(MouseEvent mouseEvent)
    {
        double mouseLat = getLat(mouseEvent.getY());
        double mouseLon = getLon(mouseEvent.getX());
        centerOn(new Point(mouseLat, mouseLon));
    }

    public void centerOn (Point p) {
        leftLon = p.getLon() - ((width / 2) / scale);
        bottomLat = p.getLat() - ((height / 2) / scale);
    }

    public double getLon (int x) {
        return leftLon + Math.round(x / scale);
    }

    public double getLat (int y) {
        return bottomLat + (height - y) / scale;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setColor (Color c) {
        gfx.setColor(c);
    }

    public void drawLine (Point a, Point b)
    {
        gfx.drawLine(getX(a), getY(a), getX(b), getY(b));
    }

    public void drawArc (Point center, double radius, double startAngle, double arcRadians)
    {
        int x = getXPlus(center, -radius);
        int y = getYPlus(center, -radius);
        gfx.drawArc(x, y, scaledInt(radius * 2), scaledInt(radius * 2), (int)Math.floor(Math.toDegrees(startAngle)), (int)Math.floor(Math.toDegrees(arcRadians)));

    }

    public boolean isLargeScale () {
        return scale > 5;
    }

    public boolean showDebug() {
        return this.showDebug;
    }
}