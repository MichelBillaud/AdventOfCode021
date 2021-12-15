package chitonpart1;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

/**
 * Advent of Code 2021 - Day 15 - Chiton 
 * Part 1 only
 * @author billaud
 */
public class ChitonPart1 {

    public static void main(String[] args) throws IOException {
        System.out.println("AOC 2021 - Day 15 - part 1");
        part1("../small.txt");
        part1("../large.txt");
    }

    private static void part1(String filename) throws IOException {
        var cave = new Cave(filename);
        int r = cave.minimalRisk();
        System.out.format("Part 1, minimal risk for '%s' is %d\n",
                filename, r);
    }

    /**
     * The Cave class
     */
    private static class Cave {

        final int[][] riskMap;
        final int rows, cols;

        public Cave(String filename) throws IOException {
            riskMap = Files.lines(Paths.get(filename))
                    .map(line -> Arrays.stream(line.split(""))
                    .mapToInt(Integer::parseInt)
                    .toArray())
                    .toArray(int[][]::new);
            rows = riskMap.length;
            cols = riskMap[0].length;

        }

        void dump() {
            System.out.println("Map\n");
            for (var row : riskMap) {
                System.out.format("%s\n", Arrays.toString(row));
            }
        }

        static record Position(int r, int c) {

        }

        static final int delta[] = {0, 1, 0, -1, 0};

        private int minimalRisk() {
            final boolean[][] visited = new boolean[rows][cols];
            final int[][] risk = new int[rows][cols];
            for (var row : risk) {
                Arrays.fill(row, Integer.MAX_VALUE);
            }

            var queue = new HashSet<Cave.Position>();
            queue.add(new Position(0, 0));
            visited[0][0] = true;
            risk[0][0] = 0;

            while (true) {
                Optional<Position> op = queue.stream().min((p1, p2)
                        -> risk[p1.r()][p1.c()] - risk[p2.r()][p2.c()]);
                if (op.isEmpty()) {
                    break;
                }
                Position p = op.get();
                queue.remove(p);
                int r = p.r();
                int c = p.c();
                visited[r][c] = true;
                for (int d = 0; d < 4; d++) {
                    int vr = r + delta[d];
                    int vc = c + delta[d + 1];
                    if (vr < 0 || vr >= rows || vc < 0 || vc >= cols
                            || visited[vr][vc]) {
                        continue;
                    }
                    queue.add(new Position(vr, vc));
                    int newRisk = risk[r][c] + riskMap[vr][vc];
                    if (newRisk < risk[vr][vc]) {
                        risk[vr][vc] = newRisk;
                    }
                }

            }
            /*
            System.out.println("Risks");
            for (var row : risk) {
                System.out.println(Arrays.toString(row));

            }
             */
            return risk[rows - 1][cols - 1];
        }

    }
    // end static class Cave

}
