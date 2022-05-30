package path;

import java.util.ArrayList;
import java.util.List;

import track.StraightTrack;
import track.Track;

public class Path 
{
    List<Track> pieces = new ArrayList<>();
    double length = 0.0;

    public static Path findPath (Track from, Track to) {
        // TODO implement
        return null;
    }

    private Path () { }

    private Path (List<Track> existingPieces, double existingLength) {
        pieces = new ArrayList<>(existingPieces);
        length = existingLength;
    }

    public boolean isStraight () {
        boolean result = true;
        for (Track piece : pieces)
            result &= (piece instanceof StraightTrack);
        return result;
    }

    public Path clone () {
        return new Path(pieces, length);
    }
}
