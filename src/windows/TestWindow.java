package windows;

import javax.swing.JFrame;
import javax.swing.event.MouseInputAdapter;

import track.CurvedTrack;
import track.Point;
import track.StraightTrack;
import track.Track;
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
*/
pieces.add(StraightTrack.create(new Point(0, 0), new Point(0, 30)));
pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 100, Math.PI / 3));
pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 100, Math.PI / 3));

/*        
        pieces.add(StraightTrack.create(new Point(0, 0), new Point(5, 5)));
        for (int i = 0; i < 10; i++)
        {
            switch ((int)(Math.ceil(Math.random() * 2)))
            {
                case 1:
                    pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 80 + (Math.random() * 80), Math.PI * (Math.random() / 4)));
                    break;

                default:
                    pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 80 + (Math.random() * 80), Math.PI * (Math.random() / 4)));
                    break;

                //default:
                //    pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 10 + (Math.random() * 40)));
                //    break;
            }
        }
//*/
        Consist test1 = Consist.createDebugConsist(1, false);
        test1.place(pieces.get(1).getEnd(0), 97);
        consists.add(test1);

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
