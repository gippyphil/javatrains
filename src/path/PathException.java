package path;

import track.Track;

public class PathException extends Exception {
    
    protected Track location;

    public PathException (Track location, String message) {
        super(message);
        this.location = location;
    }

    @Override 
    public String toString () {
        return super.toString() + " @ " + location.toString();
    }
}
