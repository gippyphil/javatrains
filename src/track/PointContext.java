package track;

public class PointContext extends Point {
    protected Track track;
    protected TrackEnd end;
    protected double distanceFromEnd;

    public PointContext(double lat, double lon, Track track, TrackEnd end, double distanceFromEnd) {
        super(lat, lon);
        this.track = track;
        this.end = end;
        this.distanceFromEnd = distanceFromEnd;
    }

    public PointContext(Point origin, double angle, double distance, Track track, TrackEnd end, double distanceFromEnd) {
        super(origin, angle, distance);
        this.track = track;
        this.end = end;
        this.distanceFromEnd = distanceFromEnd;
    }


    public Track getTrack () {
        return track;
    }

    public TrackEnd getEnd () {
        return end;
    }

    public double getDistanceFromEnd () {
        return distanceFromEnd;
    }
}
