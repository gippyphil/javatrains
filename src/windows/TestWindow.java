package windows;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import path.PathException;
import track.CurvedTrack;
import track.Point;
import track.StraightTrack;
import track.Track;
import track.TrackException;
import track.Turnout;
import train.Consist;

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
        testTurnouts();
        //testCurves();


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

        pieces.add(StraightTrack.create(new Point(5, 0), new Point(10, 0)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(5, 10), new Point(10, 10)));
        pieces.add(lastTurnout = Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(5, 20), new Point(10, 20)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_MEDIUM));

        pieces.add(StraightTrack.create(new Point(-190, 30), new Point(10, 30)));
        pieces.add(lastTurnout = Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));


        // yard ladder
        pieces.add(lastTurnout2 = Turnout.createRight(lastTurnout.getEnd(1), Turnout.RADIUS_FAST));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_MEDIUM, lastTurnout2.getDivergentArcRadians() * 2));
        pieces.add(placement = StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(2), Track.Direction.RIGHT, Turnout.RADIUS_FAST, lastTurnout2.getDivergentArcRadians()));
        pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        pieces.add(lastTurnout2 = Turnout.createRight(lastTurnout.getEnd(2), Turnout.RADIUS_FAST));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_FAST, lastTurnout.getDivergentArcRadians()));
        pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        pieces.add(StraightTrack.create(lastTurnout2.getEnd(2), 200));



        pieces.add(StraightTrack.create(new Point(5, 40), new Point(10, 40)));
        pieces.add(Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_SLOW));

        pieces.add(StraightTrack.create(new Point(5, 50), new Point(10, 50)));
        pieces.add(Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_SLOW));


        pieces.add(StraightTrack.create(new Point(-5, 0), new Point(-10, 0)));
        pieces.add(Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(-5, 10), new Point(-10, 10)));
        pieces.add(Turnout.create(pieces.get(pieces.size() - 1).getEnd(1), Track.Direction.LEFT, Turnout.RADIUS_FAST, Turnout.RADIUS_MEDIUM));

        Consist test1 = Consist.createDebugConsist(20, true);
        test1.place(placement.getEnd(1), 30);
        consists.add(test1);

    }
 
}
