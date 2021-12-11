package octopus;

// Advent of Code 2021 - Day 11 - the clean version

import java.io.IOException;

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
        Board board = new Board(filename);
        int total = 0;
        board.print("Loaded");

        for (int i = 1; i <= nbSteps; i++) {
            total += board.step().nbFlashed();
        }
        System.out.format("Part 1: file '%s', after %d steps,"
                + " a total of %d octopus flashed\n",
                filename, nbSteps, total);
    }

    private static void part2(String filename) throws IOException {
        Board board = new Board(filename);
        board.print("just loaded");

        int nbSteps = 0;
        while (!board.isSynchronized()) {
            board.step();
            nbSteps++;
        }
        System.out.format("Part #2 file '%s', syncho after %d steps\n",
                filename, nbSteps);
    }

}
