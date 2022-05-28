package train;

import track.Point;
import track.Track;

public class Wheel {

    // the track this wheel is on
    protected Track track;
    // the position in space of this wheel
    protected Point location;
    // the vehicle this wheel is attached to
    protected Vehicle parent;

    protected Wheel (Vehicle parent) {
        this.parent = parent;
    }
}
