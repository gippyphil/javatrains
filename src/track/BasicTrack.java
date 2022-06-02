package track;

import windows.Viewport;

/**
 * BasicTrack always has a single path between two ends
 */
public abstract class BasicTrack extends Track {

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
        for (TrackEnd end : ends)
        {
            //if (end.connectedEnd == null)
                end.render(v);
            //else if (end.id < end.connectedEnd.id)    
            //    end.render(v);
        }
    }

}
