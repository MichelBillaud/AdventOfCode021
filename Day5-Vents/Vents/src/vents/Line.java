package vents;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Line(Position start, Position end) {

    Line(int[] a) {
        this(new Position(a[0], a[1]),
                new Position(a[2], a[3]));
    }

    boolean isHorizontal() {
        return start.row() == end.row();
    }

    boolean isVertical() {
        return start.col() == end.col();
    }

    boolean isHorizontalOrVertical() {
        return isHorizontal() || isVertical();
    }

    Stream<Position> allPositions() {
        final int dr = start.dRow(end);
        final int dc = start.dCol(end);
        final int d = start.distance(end);
        return IntStream.rangeClosed(0, d).mapToObj(
                k -> new Position(start.row() + k * dr,
                        start.col() + k * dc));
    }
}
