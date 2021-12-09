package smokebasins;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SmokeBasins {

    public static void main(String[] args) throws IOException {
        // part1("../small.txt");
        // part1("../large.txt");
        part2("../small.txt");
        part2("../large.txt");

    }

    // Doing it the old way
    private static void part1(String filename) throws IOException {
        var data = readData(filename);
        for (var d : data) {
            System.out.println(Arrays.toString(d));
        }
        int dr[] = {-1, 0, 1, 0};
        int dc[] = {0, 1, 0, -1};
        int rows = data.length;
        int cols = data[0].length;
        int totalRisk = 0;
        for (int r = 0; r < rows; r++) {
            loop_over_c:
            for (int c = 0; c < cols; c++) {
                for (int dir = 0; dir < 4; dir++) {
                    int vr = r + dr[dir];
                    int vc = c + dc[dir];
                    if (0 <= vr && vr < rows && 0 <= vc && vc < cols
                            && (data[vr][vc] <= data[r][c])) {
                        continue loop_over_c;
                    }
                }
                //System.out.println("min at " + r + "," + c + " = " + data[r][c]);
                totalRisk += data[r][c] + 1;
            }
        }
        System.out.println("totalRisk = " + totalRisk);

    }

    // using stream operations
    
    private static void part2(String filename) throws IOException {
        var data = readData(filename);
        /*
        for (var d : data) {
            System.out.println(Arrays.toString(d));
        }
         */

        final int rows = data.length;
        final int cols = data[0].length;

        Function<Position, Integer> height
                = p -> data[p.row()][p.col()];

        BiPredicate<Position, Position> isOver
                = (p, q) -> height.apply(p) > height.apply(q);

        Predicate<Position> isLowPoint = (Position p)
                -> p.neighboursInGrid(rows, cols)
                        .noneMatch(v -> isOver.test(p, v));

        var lowPoints = Position.allPositionsInGrid(rows, cols)
                .filter(isLowPoint)
                .collect(Collectors.toCollection(HashSet::new));

        var totalRisk = lowPoints.stream()
                .mapToInt(p -> height.apply(p) + 1)
                .sum();
        System.out.format("Part #1 answer for '%s' = %d\n", filename, totalRisk);

        // calcul des versants (domains)
        final Map<Position, Set<Position>> domains = new HashMap<>();
        lowPoints.forEach(lowPoint -> {
            Set<Position> domain = new HashSet<>();
            Queue<Position> todo = new LinkedList<>();
            domain.add(lowPoint);
            todo.add(lowPoint);
            while (!todo.isEmpty()) {
                var p = todo.remove();
                p.neighboursInGrid(rows, cols)
                        .filter(d -> height.apply(d) < 9
                        && !domain.contains(d)
                        && isOver.test(d, p))
                        .forEach(d -> {
                            domain.add(d);
                            todo.add(d);
                        });
            }
            domains.put(lowPoint, domain);
        });
        // calcul du nombre de versants pour chaque point
        final int nb[][] = new int[rows][cols];
        for (var d : domains.values()) {
            for (var p : d) {
                nb[p.row()][p.col()] += 1;
            }
        }

        // bassin pour chaque lowPoint : points qui n'appartiennent 
        // qu'à son versant
        Long answer = domains.entrySet().stream()
                .map(entry
                        -> entry.getValue().stream()
                        .filter(p -> nb[p.row()][p.col()] == 1)
                        .count())
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .reduce((x, y) -> x * y).get();
        System.out.format("Part #2 answer for file '%s' = %d\n", filename, answer);
    }

    static int[] listOfNumbers(String string) {
        return Arrays.asList(string.trim().split("")).stream()
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    static int[][] readData(String filename) throws IOException {
        return Files.lines(Paths.get(filename))
                .map(SmokeBasins::listOfNumbers)
                .toArray(int[][]::new);
    }
}
