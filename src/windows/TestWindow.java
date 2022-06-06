package windows;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import track.CurvedTrack;
import track.Point;
import track.StraightTrack;
import track.Track;
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

        Turnout lastTurnout, lastTurnout2 = null;
        pieces.add(StraightTrack.create(new Point(5, 0), new Point(10, 0)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(5, 10), new Point(10, 10)));
        pieces.add(lastTurnout = Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_FAST));

        pieces.add(StraightTrack.create(new Point(5, 20), new Point(10, 20)));
        pieces.add(lastTurnout = Turnout.createLeft(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_MEDIUM));

        pieces.add(StraightTrack.create(new Point(5, 30), new Point(10, 30)));
        pieces.add(lastTurnout = Turnout.createRight(pieces.get(pieces.size() - 1).getEnd(1), Turnout.RADIUS_MEDIUM));

        pieces.add(lastTurnout2 = Turnout.createRight(lastTurnout.getEnd(1), Turnout.RADIUS_MEDIUM));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_MEDIUM, Math.toRadians(25)));
        pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(2), Track.Direction.RIGHT, Turnout.RADIUS_MEDIUM, Math.toRadians(12.5)));
        pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 200));
        pieces.add(lastTurnout2 = Turnout.createRight(lastTurnout.getEnd(2), Turnout.RADIUS_MEDIUM));
        pieces.add(CurvedTrack.create(lastTurnout2.getEnd(1), Track.Direction.RIGHT, Turnout.RADIUS_MEDIUM, Math.toRadians(12.5)));
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


/*
        pieces.add(StraightTrack.create(new Point(0, 0), new Point(0, 90)));
        pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 20));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 60, Math.toRadians(5)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 60, Math.toRadians(15)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 60, Math.toRadians(20)));

        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 50, Math.toRadians(10)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 50, Math.toRadians(10)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 50, Math.toRadians(10)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 50, Math.toRadians(10)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 100, Math.PI / 3));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 70, Math.PI * 0.75));


        pieces.add(StraightTrack.create(new Point(0, 0), new Point(0, 30)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 100, Math.PI / 3));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 100, Math.PI / 3));

        
        pieces.add(StraightTrack.create(new Point(0, 0), new Point(5, 5)));
        for (int i = 0; i < 100; i++)
        {
            switch ((int)(Math.ceil(Math.random() * 2)))
            {
                case 1:
                    pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 60 + (Math.random() * 80), Math.PI * 0.05 + (Math.random() / 2)));
                    break;

                default:
                    pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 60 + (Math.random() * 80), Math.PI * 0.05 + (Math.random() / 2)));
                    break;

                //default:
                //    pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 10 + (Math.random() * 40)));
                //    break;
            }
        }

        Consist test1 = Consist.createDebugConsist(5, false);
        test1.place(pieces.get((int)Math.floor(Math.random() * pieces.size() * 0.5)).getEnd(0), 1);
        consists.add(test1);
*/
        repaint();
        setVisible(true);
    }

    @Override
    public void paint (Graphics gfx) {
System.out.println("Rendering");
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

}
