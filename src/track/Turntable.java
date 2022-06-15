package track;

import java.util.List;

import path.PathException;
import windows.Viewport;

public class Turntable extends Junction {

    protected double length;
    protected Point pivotPoint;


    public static Turntable create (TrackEnd end, double length) throws TrackException {
        Turntable t = new Turntable();

        return t;
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
    public List<TrackEnd> pathsFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<BasicTrack> tracksFrom(TrackEnd start) throws TrackException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public double getLength() {
        // TODO Auto-generated method stub
        return length;
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
    public PointContext getPointFrom(PointContext previousPivot, TrackEnd start, double distance)
            throws PathException, TrackException {
        // TODO Auto-generated method stub
        return null;
    }
    
}
