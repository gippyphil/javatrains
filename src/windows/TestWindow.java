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
        viewport = new Viewport(2000, 1500);

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


        pieces.add(StraightTrack.create(new Point(0, 0), new Point(0, 100)));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 100, Math.PI / 2));
        pieces.add(CurvedTrack.create(pieces.get(0).getEnd(0), CurvedTrack.Direction.RIGHT, 100, Math.PI * 0.75));

        consists.add(Consist.createDebugConsist(3, true));
        consist.place(pieces.get(0).getEnd(0), 10);

/*        
        pieces.add(StraightTrack.create(new Point(0, 0), new Point(5, 5)));
        for (int i = 0; i < 500; i++)
        {
            switch ((int)(Math.ceil(Math.random() * 4)))
            {
                case 3:
                    pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 20 + (Math.random() * 20), Math.PI * (Math.random() / 4)));
                    break;

                case 2:
                    pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 20 + (Math.random() * 20), Math.PI * (Math.random() / 4)));
                    break;

                default:
                    pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), 10 + (Math.random() * 20)));
                    break;
            }
        }
//*/
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
