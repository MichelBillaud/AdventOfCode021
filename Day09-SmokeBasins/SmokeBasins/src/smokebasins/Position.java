package smokebasins;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public record Position(int row, int col) {

    Stream<Position> neighbours() {
        return Stream.of(
                new Position(row, col -1),
                new Position(row, col + 1),
                new Position(row + 1, col),
                new Position(row - 1, col)
        );
    }

    boolean insideGrid(int rows, int cols) {
        var inside = (0 <= row) && (row < rows) 
                && (0 <= col) && (col < cols);
        return inside;
    }

    public Stream<Position> neighboursInGrid(int rows, int cols) {
        return neighbours()
                .filter(v -> v.insideGrid(rows, cols));
    }

    static Stream<Position> allPositionsInRow(int r, int cols) {
        return IntStream.range(0, cols)
                .mapToObj(c -> new Position(r, c));
    }

    static Stream<Position> allPositionsInGrid(int rows, int cols) {
        return IntStream.range(0, rows)
                .mapToObj(r -> r)
                .flatMap(r -> allPositionsInRow(r, cols));
    }
}
