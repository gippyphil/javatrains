package windows;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import path.PathException;
import track.BasicTrack;
import track.CurvedTrack;
import track.Point;
import track.SplineTrack;
import track.StraightTrack;
import track.Track;
import track.TrackEnd;
import track.TrackException;
import track.Turnout;
import track.Turntable;
import train.Consist;
import train.Vehicle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;

import java.util.ArrayList;
import java.util.List;

public class TestWindow extends JFrame {
    
    protected Viewport viewport;

    protected List<Track> pieces;
    protected List<Consist> consists;
    
    public static void main (String args[]) throws Exception {
        new TestWindow();
    }


    protected TestWindow () throws Exception {

        setSize(2000, 1500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewport = new Viewport(2000, 1500, true);

        addMouseWheelListener((mouseWheelEvent) -> {
            viewport.zoom(mouseWheelEvent);
            repaint();
        });



        addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked (MouseEvent event)
            {
                System.out.format("%d: LatLon: %1.1f, %1.1f\n", event.getButton(), viewport.getLat(event.getY()), viewport.getLon(event.getX()));
                viewport.center(event);
                repaint();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent componentEvent) {
                viewport.setSize(getContentPane().getWidth(), getContentPane().getHeight());
            }
        });


    
        pieces = new ArrayList<>();
        consists = new ArrayList<>();
        //testTurnouts();
        //testCurves();
        testAssembly();


        repaint();
        setVisible(true);
    }

    @Override
    public void paint (Graphics gfx) {
//System.out.println("Rendering");
        gfx.setColor(Color.BLACK);
        Dimension size = getSize();
        gfx.fillRect(0, 0, size.width, size.height);
        
        if (viewport != null)
        {
            viewport.setGraphics(gfx);

            pieces.forEach((piece) -> piece.render(viewport));

            consists.forEach((consist) -> consist.render(viewport));
        }
    }


    private void testCurves () throws TrackException, PathException { 
        StraightTrack straight = StraightTrack.create(new Point(-20, 0), new Point(-23, 20));
        CurvedTrack curved = CurvedTrack.create(straight.getEnd(1), Track.Direction.LEFT, 16, Math.PI / 3);

        pieces.add(straight);
        pieces.add(curved);
        pieces.add(StraightTrack.create(curved.getEnd(1), 20));

        Consist test1 = Consist.createDebugConsist(1, false);
        test1.place(straight.getEnd(0), 12.5);
        consists.add(test1);
/* 
        StraightTrack straight2 = StraightTrack.create(new Point(0, 30), new Point(20, 30));
        CurvedTrack curved2 = CurvedTrack.create(straight2.getEnd(1), Track.Direction.RIGHT, 20, Math.PI / 2);

        pieces.add(straight2);
        pieces.add(curved2);

        Consist test2 = Consist.createDebugConsist(1, false);
        test2.place(curved2.getEnd(1), 15);
        consists.add(test2);
*/
    }

    private void testTurnouts () throws TrackException, PathException {
        Turnout lastTurnout, lastTurnout2 = null;
        StraightTrack placement = null;
        Turntable turntable = null;
        List<BasicTrack> sidings = new ArrayList<>();
        BasicTrack last = null;

        // yard ladder
        pieces.add(last = StraightTrack.create(new Point(-500, -10), new Point(10, -10)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(last = StraightTrack.create(lastTurnout.getEnd(1), 520));
        sidings.add(last);
        pieces.add(lastTurnout = Turnout.createRight(lastTurnout.getEnd(2), Turnout.RADIUS_FAST));
        pieces.add(last = StraightTrack.create(lastTurnout.getEnd(2), 490));
        sidings.add(last);
        pieces.add(lastTurnout = Turnout.createRight(lastTurnout.getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(last = StraightTrack.create(lastTurnout.getEnd(2), 460));
        sidings.add(last);
        pieces.add(lastTurnout = Turnout.createRight(lastTurnout.getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(last = StraightTrack.create(lastTurnout.getEnd(2), 430));
        sidings.add(last);
        pieces.add(lastTurnout = Turnout.createRight(lastTurnout.getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(last = StraightTrack.create(lastTurnout.getEnd(2), 400));
        sidings.add(last);
        pieces.add(CurvedTrack.create(lastTurnout.getEnd(1), Track.Direction.LEFT, Turnout.RADIUS_MEDIUM, Math.PI / 3 - lastTurnout.getDivergentArcRadians()));
        pieces.add(turntable = Turntable.create(pieces.get(pieces.size() - 1).getEnd(1), 28, 1 + (int)(Math.random() * 30)));

        for (int i = 1; i < turntable.getEnds().size(); i++) {
            StraightTrack tt = StraightTrack.create(turntable.getEnd(i), 25);
            pieces.add(tt);

            if (Math.random() > 0.75) {
                Consist loco = new Consist("Loco" + i, new Vehicle(17.0, 2.4, 2.4));
                loco.place(tt.getEnd((int)Math.round(Math.random())), 1 /*Math.random() * (tt.getLength() - loco.getLength())*/);
                consists.add(loco);
            }
        }

        pieces.add(StraightTrack.create(new Point(5, 10), new Point(10, 10)));
        pieces.add(lastTurnout = Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(5, 20), new Point(10, 20)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_MEDIUM));

        pieces.add(StraightTrack.create(new Point(-190, 30), new Point(10, 30)));
        pieces.add(lastTurnout = Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));




        // yard ladder
        pieces.add(lastTurnout2 = Turnout.createRight(lastTurnout.getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_MEDIUM, lastTurnout2.getDivergentArcRadians() * 2));
        pieces.add(last = StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        sidings.add(last);
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(2), Track.Direction.RIGHT, Turnout.RADIUS_FAST, lastTurnout2.getDivergentArcRadians()));
        pieces.add(last = StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        sidings.add(last);
        pieces.add(lastTurnout2 = Turnout.createRight(lastTurnout.getEnd(2), Turnout.RADIUS_FAST));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_FAST, lastTurnout.getDivergentArcRadians()));
        pieces.add(last = StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        sidings.add(last);
        pieces.add(last = StraightTrack.create(lastTurnout2.getEnd(2), 200));
        sidings.add(last);



        pieces.add(StraightTrack.create(new Point(5, 40), new Point(10, 40)));
        pieces.add(Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_SLOW));

        pieces.add(StraightTrack.create(new Point(5, 50), new Point(10, 50)));
        pieces.add(Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_SLOW));


        // Wyes
        pieces.add(StraightTrack.create(new Point(5, 60), new Point(10, 60)));
        pieces.add(lastTurnout = Turnout.create(pieces.get(pieces.size() - 1).getEnd(1), Track.Direction.WYE, Turnout.RADIUS_SLOW, Turnout.RADIUS_SLOW));

        pieces.add(StraightTrack.create(new Point(5, 70), new Point(10, 70)));
        pieces.add(lastTurnout = Turnout.create(pieces.get(pieces.size() - 1).getEnd(1), Track.Direction.WYE, Turnout.RADIUS_MEDIUM, Turnout.RADIUS_MEDIUM));

        pieces.add(StraightTrack.create(new Point(5, 80), new Point(10, 80)));
        pieces.add(lastTurnout = Turnout.create(pieces.get(pieces.size() - 1).getEnd(1), Track.Direction.WYE, Turnout.RADIUS_FAST, Turnout.RADIUS_FAST));



        pieces.add(StraightTrack.create(new Point(-5, 10), new Point(-10, 10)));
        pieces.add(Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(-5, 60), new Point(-10, 60)));
        pieces.add(Turnout.create(pieces.get(pieces.size() - 1).getEnd(1), Track.Direction.LEFT, Turnout.RADIUS_FAST, Turnout.RADIUS_MEDIUM));

        for (BasicTrack siding : sidings) {
            if (Math.random() > 0.8)
                continue;

            Consist test = Consist.createDebugConsistToLength(siding.getLength() * 0.8, true, 0.95);
            double remainingLength = siding.getLength() * 0.8 - test.getLength();
            test.place(siding.getEnd(1), remainingLength * Math.random());
            consists.add(test);
        }





        // splines
        StraightTrack s1, s2;
        pieces.add(s1 = StraightTrack.create(new Point(20, 130), new Point(60, 130)));
        pieces.add(s2 = StraightTrack.create(new Point(160, 115), new Point(195, 130)));
        pieces.add(SplineTrack.create(s1.getEnd(1), s2.getEnd(0)));

        Consist splineTest = Consist.createDebugConsistToLength(s2.getLength(), false, 1.0);
        splineTest.place(s2.getEnd(1), 30);
        consists.add(splineTest);
        viewport.centerOn(s2.getEnd(0).getLoc());
    }

    private void testAssembly () throws TrackException, PathException {
        Turnout lastTurnout = null;
        StraightTrack lastStraight = null;
        CurvedTrack lastCurve = null;

        // passing loop
        pieces.add(lastStraight = StraightTrack.create(new Point(-100, -10), new Point(10, -10)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(lastStraight = StraightTrack.create(lastTurnout.getEnd(1), 100));
        pieces.add(lastCurve = CurvedTrack.create(lastTurnout.getEnd(2), Track.Direction.RIGHT, Turnout.RADIUS_FAST, lastTurnout.getDivergentArcRadians()));
        pieces.add(lastStraight = StraightTrack.create(lastCurve.getEnd(1), 100));
        pieces.add(lastCurve = CurvedTrack.create(lastStraight.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_FAST, lastTurnout.getDivergentArcRadians()));


        
        pieces.add(lastTurnout = Turnout.createRight(TrackEnd.create(null, new Point(0, 0), 0), Turnout.RADIUS_SLOW));

        pieces.add(lastStraight = StraightTrack.create(new Point(0, 10), new Point(30, 10)));
        pieces.add(lastCurve = CurvedTrack.create(TrackEnd.create(null, new Point(0, 20), Math.PI / 3), Track.Direction.RIGHT, Turnout.RADIUS_SLOW, lastTurnout.getDivergentArcRadians()));
        lastStraight.moveAndConnect(lastStraight.getEnd(1), lastTurnout.getEnd(1), true);
        lastCurve.moveAndConnect(lastCurve.getEnd(1), lastTurnout.getEnd(2), true);
    }
}
