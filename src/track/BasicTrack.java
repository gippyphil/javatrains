package track;

import java.awt.Color;

import windows.Viewport;

/**
 * BasicTrack always has a single path between two ends
 */
public abstract class BasicTrack extends Track {

    // set when this basic track is a component of a junction, etc
    protected Track parent;

    @Override
    public String toString () {
        return String.format("%03d:%s {%s -> %s (%1.3fm)}", id, this.getClass().getCanonicalName(), ends.get(0), ends.get(1), getLength());
    }

    public TrackEnd pathFrom (TrackEnd start) throws TrackException {
        if (!ends.contains(start))
            throw new TrackException(String.format("%s does not contain end %s", this, start));
        // return the other end
        return ends.get((ends.indexOf(start) + 1) % 2);
    }

    public void render (Viewport v)
    {
        if (v.showDebug()) {
            if (parent == null) {
                for (TrackEnd end : ends)
                {
                    //if (end.connectedEnd == null)
                        end.render(v);
                    //else if (end.id < end.connectedEnd.id)    
                    //    end.render(v);
                }
            }
            Point textPoint = new Point(ends.get(1).getLoc(), Point.add(ends.get(1).getAng(), Math.PI * 0.75), Track.GAUGE * 2);
            v.setColor(Color.LIGHT_GRAY);
            if (v.isLargeScale())
                v.getGraphics().drawString("T" + id, v.getX(textPoint), v.getY(textPoint));
        }
    }

}
