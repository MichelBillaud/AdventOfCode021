package octopus;

// tAOC 2021 - Day 11 - he quick and really dirty version

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Stack;

public class Octopus {

    public static void main(String[] args) throws IOException {
        part1("../test1.txt", 2);
        part1("../small.txt", 100);
        part1("../large.txt", 100);
        part2("../small.txt");
        part2("../large.txt");
    }

    static int[] dr = {-1, -1, -1, 0, 1, 1, 1, 0};
    static int[] dc = {1, 0, -1, -1, -1, 0, 1, 1};

    private static void part1(String filename, int nbSteps) throws IOException {
        final int[][] grid = readGrid(filename);
        final int rows = grid.length;
        final int cols = grid[0].length;

        int total = 0;
        print("Loaded", grid);

        for (int i = 1; i <= nbSteps; i++) {

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
                    // ajouter tous ses voisins
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
            print("after step " + i + ", "
                    + nbFlashed + " octopus flashed", grid);
            total += nbFlashed;
        }
        System.out.format("Part 1: file '%s', after %d steps,"
                + " a total of %d octopus flashed\n",
                filename, nbSteps, total);
    }

    private static void part2(String filename) throws IOException {
        final int[][] grid = readGrid(filename);
        final int rows = grid.length;
        final int cols = grid[0].length;

        print("avant", grid);

        int nbSteps = 0;
        while (true) {
            boolean all_zeroes = true;
            check_all_zeroes:
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    if (grid[r][c] != 0) {
                        all_zeroes = false;
                        break check_all_zeroes;
                    }
                }
            }
            if (all_zeroes) break;
            nbSteps += 1;
            var toIncrease = new Stack<Position>();
            for (int r = 0; r < rows; r++) {
                for (int c = 0; c < cols; c++) {
                    toIncrease.add(new Position(r, c));
                }
            }

            while (!toIncrease.isEmpty()) {
                var p = toIncrease.pop();
                if (++grid[p.r][p.c] == 10) {
                    // ajouter tous ses voisins
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
        }
        System.out.format("Part #2 file '%s', syncho after %d steps\n",
                filename, nbSteps);
    }

    static record Position(int r, int c) {

    }

    private static void print(String message, int[][] grid) {
        System.out.println("--- " + message);
        for (var line : grid) {
            System.out.println(Arrays.toString(line));
        }
    }

    private static int[][] readGrid(String filename) throws IOException {
        int[][] grid = Files.lines(Paths.get(filename))
                .map(line -> line.split(""))
                .map(array -> Arrays.stream(array)
                .mapToInt(Integer::parseInt)
                .toArray()
                )
                .toArray(int[][]::new);
        return grid;
    }

}
