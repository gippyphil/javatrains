package track;

public class TrackException extends Exception {

    protected Track focus;

    public TrackException (Track focus, String message) {
        super(message);
        this.focus = focus;
    }

    public TrackException (String message) {
        super(message);
        this.focus = null;
    }


    public Track getFocusTrack () {
        return focus;
    }
}
