package windows;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import java.awt.event.MouseEvent;

public class TestCircles extends JFrame {

    public TestCircles () {
        super("Test Circles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked (MouseEvent event) {
                if (event.getClickCount() > 1) {
                    arcX = Math.round((event.getX() - zeroX) / STEP);
                    arcY = Math.round((zeroY - event.getY()) / STEP);
                    repaint();
                }
                else if (event.getButton() == MouseEvent.BUTTON1) {
                    lineX1 = Math.round((event.getX() - zeroX) / STEP);
                    lineY1 = Math.round((zeroY - event.getY()) / STEP);
                    repaint();
                }
                else if (event.getButton() == MouseEvent.BUTTON3) {
                    lineX2 = Math.round((event.getX() - zeroX) / STEP);
                    lineY2 = Math.round((zeroY - event.getY()) / STEP);
                    repaint();
                }
            }
        });
    }

    public static void main (String args[]) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TestCircles tc = new TestCircles();
                tc.setSize(2000, 1400);
                tc.setVisible(true);
            }
        });    
    }

    public static final double STEP = 100.0;
    protected int zeroX;
    protected int zeroY;

    double lineX1 = 0, lineY1 = 6, lineX2 = 0, lineY2 = -1;
    double arcX = 1, arcY = 1, radius = 3;

    @Override
    public void paint (Graphics g) {

        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        zeroY = getHeight() / 2;
        zeroX = getWidth() / 2;


        g.setColor(Color.DARK_GRAY);
        int step = 0;
        int y = zeroY;
        while (y > 0 && y < getHeight()) {
            g.drawLine(0, y, getWidth(), y);
            step++;
            y += (STEP * step * ((step % 2== 0) ? -1 : 1));
        }
        step = 0;
        int x = zeroX;
        while (x > 0 && x < getWidth()) {
            g.drawLine(x, 0, x, getHeight());
            step++;
            x += (STEP * step * ((step % 2== 0) ? -1 : 1));
        }
        
        g.setColor(Color.GRAY);
        g.drawLine(0, zeroY, getWidth(), zeroY);
        g.drawLine(zeroX, 0, zeroX, getHeight());


//        drawLine(1, 1, 7, 3, Color.GREEN, g);
//        drawCircle(0, 0, 4, Color.YELLOW, g);

        drawIntersection(Color.MAGENTA, g);
    }




    protected boolean inRange (double min, double val, double max) {
        return (min <= val && val <= max) || (max <= val && val <= min);
    }

    protected void drawIntersection (Color c, Graphics g) {

        drawLine(lineX1, lineY1, lineX2, lineY2, Color.CYAN, g);
        drawCircle(arcX, arcY, radius, Color.PINK, g);

        double offsetY = lineY1 - arcY;
        double offsetX = lineX1 - arcX;
        double riseOverRun = (lineY2 - lineY1) / (lineX2 - lineX1);
        double x1 = Double.NaN, y1 = Double.NaN, x2 = Double.NaN, y2 = Double.NaN;
        boolean intersection1 = false;
        boolean intersection2 = false;
        if (Double.isFinite(riseOverRun)) {
            double arcZeroYOffset = offsetY - (offsetX * riseOverRun);
            double denominator = 1 + Math.pow(riseOverRun, 2);
            double sqrtPart = Math.sqrt(Math.pow(radius, 2) + Math.pow(riseOverRun, 2) * Math.pow(radius, 2) - Math.pow(arcZeroYOffset, 2));
            if (!Double.isNaN(sqrtPart))
            {
                x1 = ((-arcZeroYOffset * riseOverRun) + sqrtPart) / denominator;
                y1 = arcY + arcZeroYOffset + (x1 * riseOverRun);
                x1 += arcX;
                x2 = -(((arcZeroYOffset * riseOverRun) + sqrtPart) / denominator);
                y2 = arcY + arcZeroYOffset + (x2 * riseOverRun);
                x2 += arcX;
            }
        }
        else if (offsetX != 0.0) {
            // use pythagorus' theorm using radius and X delta from arc.
            x2 = x1 = lineX1; // or lineX2 - they are the same
            y1 = arcY - Math.sqrt(Math.pow(radius, 2) - Math.pow(offsetX, 2));
            y2 = arcY + Math.sqrt(Math.pow(radius, 2) - Math.pow(offsetX, 2));

            g.setColor(Color.white);
            g.drawString("Verical Line!", zeroX + 30, zeroY + 30);
            // no idea!
        }
        else // (just +/- radius)
        {
            x1 = x2 = arcX;
            y1 = arcY + radius;
            y2 = arcY - radius;
        }

                    
        intersection1 = inRange(lineX1, x1, lineX2) && inRange(lineY1, y1, lineY2);
        intersection2 = inRange(lineX1, x2, lineX2) && inRange(lineY1, y2, lineY2);

        g.setColor(c);

        if (intersection1) {
            int X1 = getX(x1);
            int Y1 = getY(y1);
            g.drawArc(X1 - 5, Y1 - 5, 10, 10, 0, 360);            
            g.drawLine(X1, Y1 - 10, X1, Y1 + 10);
            g.drawLine(X1 - 10, Y1, X1 + 10, Y1);
            g.drawString(String.format("%1.2f, %1.2f\n", x1, y1), X1 + 12, Y1 + 8);
        }
        if (intersection2) {
            int X2 = getX(x2);
            int Y2 = getY(y2);
            g.drawArc(X2 - 5, Y2 - 5, 10, 10, 0, 360);            
            g.drawLine(X2, Y2 - 10, X2, Y2 + 10);
            g.drawLine(X2 - 10, Y2, X2 + 10, Y2);
            g.drawString(String.format("%1.2f, %1.2f\n", x2, y2), X2 + 12, Y2 + 8);
        }
    }





    protected int getX (double x) {
        return zeroX + (int)Math.round(x * STEP);
    }

    protected int getY (double y) {
        return zeroY - (int)Math.round(y * STEP);
    }

    protected void drawLine (double x1, double y1, double x2, double y2, Color c, Graphics g) {
        g.setColor(c);
        g.drawLine(getX(x1), getY(y1), getX(x2), getY(y2));

        double riseOverRun = (y2 - y1) / (x2 - x1);

        g.setColor(Color.BLUE);
        double start = Math.floor(Math.min(x1, x2));
        double end = Math.ceil(Math.max(x1, x2));

        double xZeroYOffset = y1 - (x1 * riseOverRun);

        for (double x = start; x <= end; x += 0.5) {
            int X = getX(x);

            int Y = getY(xZeroYOffset + (x * riseOverRun));

            g.drawArc(X - 8, Y - 8, 16, 16, 0, 360);            
        }
    }

    protected void drawCircle (double arcX, double arcY, double radius, Color c, Graphics g) {
        g.setColor(c);
        g.drawArc(getX(-radius + arcX), getY(radius + arcY), (int)(2 * STEP * radius), (int)(2 * STEP * radius), 0, 360);

        g.setColor(Color.RED);

        for (double x = -20.0; x < 20.0; x += 0.25) {
            int X = getX(x + arcX);
            // double sqrtPart = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2) + (2 * arcX * radius) - Math.pow(arcX, 2));
//            if (!Double.isNaN(sqrtPart))
            {
                double y1 = Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2)) + arcY;
                double y2 = -Math.sqrt(Math.pow(radius, 2) - Math.pow(x, 2)) + arcY;

                if (!Double.isNaN(y1)) {
                    int Y1 = getY(y1);
                    g.drawArc(X - 5, Y1 - 5, 10, 10, 0, 360);            
                }
                if (!Double.isNaN(y2)) {
                    int Y2 = getY(y2);
                    g.drawArc(X - 5, Y2 - 5, 10, 10, 0, 360);            
                }
            }
        }
    }

}
