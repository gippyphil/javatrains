package track;

import animation.AnimationListener;
import path.PathException;

public abstract class Junction extends Track implements AnimationListener {

    /**
     * Configures this junction to provide a path between the specifed ends
     * @param from The start of the proposed path
     * @param to The end end of the proposed path
     * @throws PathException if the path cannot be configured
     */
    public abstract void selectPath (TrackEnd from, TrackEnd to) throws PathException;

    /**
     * Determines if a given path is selected
     * @param from The start of the proposed path
     * @param to The end end of the proposed path
     * @return true if the path is selected, false otherwise
     */
    public abstract boolean isSelected (TrackEnd from, TrackEnd to);

    /**
     * Check to see if the junction is in a transition state (such as a turntable rotating)
     * @return true if the junction is in the process of changing paths
     */
    public abstract boolean inTransition ();
}
