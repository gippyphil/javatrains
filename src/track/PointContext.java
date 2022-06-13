package track;

public class PointContext extends Point {
    protected Track track;
    protected TrackEnd end;

    public PointContext(Point point, Track track, TrackEnd end) {
        super(point.getLat(), point.getLon());
        this.track = track;
        this.end = end;
    }

    public PointContext(double lat, double lon, Track track, TrackEnd end) {
        super(lat, lon);
        this.track = track;
        this.end = end;
    }

    public PointContext(Point origin, double angle, double distance, Track track, TrackEnd end) {
        super(origin, angle, distance);
        this.track = track;
        this.end = end;
    }


    public Track getTrack () {
        return track;
    }

    public TrackEnd getEnd () {
        return end;
    }

}
