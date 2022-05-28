import java.util.ArrayList;
import java.util.List;

import track.CurvedTrack;
import track.Point;
import track.StraightTrack;
import track.Track;

public class TestApp {
    public static void main(String[] args) throws Exception {

        List<Track> pieces = new ArrayList<>();

        pieces.add(StraightTrack.create(new Point(0, 0), new Point(20, 10)));
        pieces.add(StraightTrack.create(pieces.get(pieces.size() - 1).getEnd(1), pieces.get(pieces.size() - 1).getLength() / 2));

        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.RIGHT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 5, Math.PI / 4));
        pieces.add(CurvedTrack.create(pieces.get(pieces.size() - 1).getEnd(1), CurvedTrack.Direction.LEFT, 5, Math.PI / 4));

        pieces.forEach((piece) -> System.out.println(piece));
    }
}
