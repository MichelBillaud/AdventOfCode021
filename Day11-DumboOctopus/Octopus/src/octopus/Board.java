package octopus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

public class Board {

    static int[] dr = {-1, -1, -1, 0, 1, 1, 1, 0};
    static int[] dc = {1, 0, -1, -1, -1, 0, 1, 1};

    final int[][] grid;
    final int rows;
    final int cols;

    Board(String filename) throws IOException {
        grid = readGrid(filename);
        rows = grid.length;
        cols = grid[0].length;
    }

    private static int[][] readGrid(String filename) throws IOException {
        return Files.lines(Paths.get(filename))
                .map(line -> line.split(""))
                .map(array -> Arrays.stream(array)
                .mapToInt(Integer::parseInt)
                .toArray()
                ).toArray(int[][]::new);
    }

    static record Position(int r, int c) {

    }

    public static record StepInfo(int nbFlashed) {

    }

    public StepInfo step() {
        var toIncrease = new Stack<Position>();
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                toIncrease.add(new Position(r, c));
            }
        }

        int nbFlashed = 0;
        while (!toIncrease.isEmpty()) {
            var p = toIncrease.pop();
            if (++grid[p.r][p.c] == 10) {
                nbFlashed += 1;
                // add all neighbours
                for (int d = 0; d < 8; d++) {
                    int vr = p.r + dr[d], vc = p.c + dc[d];
                    if (0 <= vr && vr < rows
                            && 0 <= vc && vc < cols) {
                        toIncrease.add(new Position(vr, vc));
                    }
                }
            }
        }
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] > 9) {
                    grid[r][c] = 0;
                }
            }
        }
        return new StepInfo(nbFlashed);
    }

    public boolean isSynchronized() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != 0) {
                    return false;
                }
            }
        }
        return true;
    }

    public void print(String message) {
        System.out.println("--- " + message);
        for (var line : grid) {
            System.out.println(Arrays.toString(line));
        }
    }
}
