package path;

import java.util.ArrayList;
import java.util.List;

import track.BasicTrack;
import track.Junction;
import track.StraightTrack;
import track.Track;
import track.TrackEnd;
import track.TrackException;

public class Path 
{
    List<Segment> pieces = new ArrayList<>();
    double length = 0.0;
    boolean discarded = false;

    private static int nextID = 0;
    protected int id;

    public static Path findMostDirectPath (Track from, Track to, boolean junctionsAsIs) throws TrackException, PathException {
        List<Path> possiblePaths = findPaths(from, to, true, junctionsAsIs);
        if (possiblePaths.size() == 0)
            return null;
        // sort by total length and return the shortest.  (yes, rounding errors, etc))
        possiblePaths.sort((left, right) -> (int)Math.ceil(left.length - right.length));
        return possiblePaths.get(0);
    }

    public static List<Path> findPaths (Track from, Track to, boolean stopAfterFirstResult, boolean junctionsAsIs) throws TrackException, PathException {
System.out.format("findPaths(%d, %d)\n", from.id, to.id);        
        // create an initial path for each direction
        List<Path> successfulPaths = new ArrayList<>();
        List<Path> possiblePaths = new ArrayList<>();
        for (TrackEnd end : from.getEnds()) {
            if (end.getConnectedEnd() != null)
                // add the inital piece, and it's connected piece
                possiblePaths.add(new Path(end, from));
        }

        while (possiblePaths.size() > 0) {
            List<Path> discardList = new ArrayList<>();
            List<Path> addList = new ArrayList<>();
            // iterate through each path.
            for (Path possiblePath : possiblePaths) {
                // if this has reached the end, we can discard it
                if (!possiblePath.isConnected()) {
                    discardList.add(possiblePath);
                    continue;
                }
                Track next = possiblePath.getEnd().getConnectedTrack();
                // if we have found the target...
                if (next == to) {
System.out.format("%d has found a solution!", possiblePath.id);                    
                    successfulPaths.add(possiblePath);
                    if (stopAfterFirstResult) {
                        discardList.addAll(possiblePaths);
                        break;
                    }
                    else {
                        discardList.add(possiblePath);
                        continue;
                    }
                }
                // keep looking
                if (next instanceof BasicTrack) {
                    possiblePath.add(next.pathFrom(possiblePath.getEnd().getConnectedEnd()), next);
                } else if (next instanceof Junction) {
                    Junction junction = (Junction)next;
                    if (junctionsAsIs) {
                        possiblePath.add(junction.pathFrom(possiblePath.getEnd().getConnectedEnd()), junction);
                    }
                    else {
                        List<TrackEnd> junctionEnds = junction.pathsFrom(possiblePath.getEnd().getConnectedEnd());
                        // we need to add a new path for all but the first option this junction gives us 
                        for (int i = 1; i < junctionEnds.size(); i++) {
                            Path newPath = possiblePath.clone();
                            newPath.add(junctionEnds.get(i), junction);
                            addList.add(newPath);
                        }
                        possiblePath.add(junctionEnds.get(0), junction);
                            
                    }
                } else { // unknown, bail!
                    throw new TrackException(next, "Unknown track type for pathing");
                }

                // should we discard this path for some reason (eg: circular path)
                if (possiblePath.isDiscarded())
                    discardList.add(possiblePath);
            }
            possiblePaths.removeAll(discardList);
            possiblePaths.addAll(addList);
        }
        return successfulPaths;
    }

    private Path (TrackEnd initialEnd, Track initialTrack) {
        id = ++nextID;
        add(initialEnd, initialTrack);
    }

    protected Path add (TrackEnd end, Track track) {
System.out.format("%d.add(%03d, %d) == %d\n", id, end.id, track.id, pieces.size());        
        pieces.add(new Segment(end, track));
        // we don't add the length of the first piece
        if (pieces.size() > 1)
            length += track.getLength();
        return this;
    }

    protected TrackEnd getEnd () {
        return this.pieces.get(this.pieces.size() - 1).end;
    }

    protected boolean isConnected () {
        return getEnd().getConnectedTrack() != null;
    }

    protected boolean isDiscarded () {
        return discarded;
    }

    protected void discard () {
        discarded = true;
    }

    private Path (List<Segment> existingPieces, double existingLength) {
        pieces = new ArrayList<>(existingPieces);
        length = existingLength;
    }

    public boolean isStraight () {
        boolean result = true;
        for (Segment piece : pieces)
            result &= (piece.track instanceof StraightTrack);
        return result;
    }

    public Path clone () {
        return new Path(pieces, length);
    }

    class Segment {

        TrackEnd end;
        Track track;

        protected Segment (TrackEnd end, Track track) {
            this.end = end;
            this.track = track;
        }
    }
}
