package track;

import java.util.List;

import path.PathException;
import windows.Viewport;

public class Turnout extends Junction {

    // a turnout is basically two (or three?) different tracks
    protected List<BasicTrack> components;

    // the fixed end for the entry side of the points
    protected TrackEnd entry;


    public static Turnout createLeftHand (TrackEnd end, double radius) throws TrackException {
        Turnout t = new Turnout();

        t.entry = TrackEnd.createAttached(t, end);
        t.ends.add(t.entry);

        // TODO: work out the End thing (connecting to straight vs curve)

        //StraightTrack straight = StraightTrack.create(end, length)

        return t;
    }





    protected Turnout () {

    }

    @Override
    public void onAnimationTimer() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void selectPath(TrackEnd from, TrackEnd to) throws PathException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isSelected(TrackEnd from, TrackEnd to) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean inTransition() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public double getLength() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public TrackEnd pathFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }





    @Override
    public void render(Viewport v) {
        // TODO Auto-generated method stub
        
    }





    @Override
    public PointContext getPointFrom(TrackEnd start, double distance) throws PathException, TrackException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
