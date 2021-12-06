package bingo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Bingo {

    static class BoardBuilder {

        int[][] numbers = new int[5][0];
        int currentLine = 0;

        void addRow(int t[]) {
            numbers[currentLine++] = t;
        }

        Board build() {
            return new Board(this);
        }
    }

    static class Board {

        final int[][] numbers;
        final Map<Integer, Position> positions = new HashMap<>();
        final boolean[][] marks = new boolean[5][5];

        Board(BoardBuilder bb) {
            numbers = bb.numbers;
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    positions.put(numbers[r][c], new Position(r, c));
                }
            }
        }

        void print() {
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    int n = numbers[r][c];
                    System.out.format("%3d%c", n, marks[r][c] ? '+' : ' ');
                }
                System.out.println();
            }
        }

        boolean play(int n) {
            Position p = positions.get(n);
            if (p == null) {
                return false;
            }
            marks[p.row()][p.col()] = true;
            boolean ar = true, ac = true;
            for (int i = 0; i < 5; i++) {
                ar = ar && marks[p.row()][i];
                ac = ac && marks[i][p.col()];
            }
            return ar || ac;
        }

        int sumOfUnmarkedPositions() {
            int sum = 0;
            for (int r = 0; r < 5; r++) {
                for (int c = 0; c < 5; c++) {
                    if (!marks[r][c]) {
                        sum += numbers[r][c];
                    }
                }
            }
            return sum;
        }

    }

    static int[] stringToIntArray(String s, String separator) {
        return Arrays.asList(s.trim().split(separator))
                .stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    public static void main(String[] args) {
        //part1("../small.txt");
        part2("../small.txt");
        part2("../large.txt");
    }

    private static int[] read_draws(Scanner in) {
        var ligneTirages = in.nextLine();
        System.out.println("tirages = " + ligneTirages);
        var tirages = stringToIntArray(ligneTirages, ",");
        System.out.println("" + tirages.length);
        return tirages;
    }

    private static ArrayList<Board> read_boards(Scanner in) {

        var boards = new ArrayList<Board>();

        while (in.hasNextLine()) {
            in.nextLine();
            BoardBuilder bb = new BoardBuilder();
            for (int r = 0; r < 5; r++) {
                bb.addRow(stringToIntArray(in.nextLine(), "\\s+"));
            }
            Board b = bb.build();
            System.out.println("---");
            b.print();
            boards.add(b);
        }
        System.out.println("-------------");
        return boards;
    }

    private static void part1(String filename) {
        try {
            var in = new Scanner(new File(filename));
            var draws = read_draws(in);
            var boards = read_boards(in);

            loop_over_draws:
            for (var d : draws) {
                System.out.println("# Tirage de " + d);
                loop_over_boards:
                for (var b : boards) {
                    boolean success = b.play(d);
                    b.print();
                    System.out.println();
                    if (success) {
                        int s = b.sumOfUnmarkedPositions();
                        System.out.format("=> coup=%d somme=%d score=%d\n",
                                d, s, d * s);
                        break loop_over_draws;
                    }
                }
            }

        } catch (FileNotFoundException ex) {
            System.err.println("Pb ouverture " + filename);
        }
    }

    private static void part2(String filename) {
        try {
            var in = new Scanner(new File(filename));
            var draws = read_draws(in);
            var boards = read_boards(in);
            Board lastWinner = null;
            int lastDraw = 0;
            loop_over_draws:
            for (var d : draws) {
                System.out.println("# Tirage de " + d);
                ArrayList<Board> losers = new ArrayList<>();
                loop_over_boards:
                for (var b : boards) {
                    boolean success = b.play(d);
                    b.print();
                    System.out.println();
                    if (success) {
                        lastWinner = b;
                        lastDraw = d;
                    } else {
                        losers.add(b);
                    }
                    boards = losers;
                }
            }

            int s = lastWinner.sumOfUnmarkedPositions();
            System.out.format("=> coup=%d somme=%d score=%d\n",
                    lastDraw, s, lastDraw * s);

        } catch (FileNotFoundException ex) {
            System.err.println("Pb ouverture " + filename);
        }
    }

}
