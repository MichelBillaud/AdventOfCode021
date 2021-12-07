package treachery;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Treachery {

        // using a dichotomy method to get the destination
        // with the minimum sum of distance to the positions
    
    public static void main(String[] args) throws FileNotFoundException {
        // part1("../small.txt");
        // part1("../large.txt");
        // part2("../small.txt");
        part2("../large.txt");
    }

    private static void part1(String filename) throws FileNotFoundException {
        List<Long> positions = readDataFile(filename);

        // je cafouille peut être un peu avec mes mapToLong. A voir
        var min = positions.stream().mapToLong(i -> i).min().getAsLong();
        var max = positions.stream().mapToLong(i -> i).max().getAsLong();

        while (min < max) {
            long middle = (min + max) / 2;
            long diff = distance(positions, middle + 1)
                    - distance(positions, middle);
            /*
            System.out.format("min=%d middle=%d max=%d diff=%d\n",
                    min, middle, max, diff);
             */
            if (diff > 0) {
                max = middle;
            } else {
                min = middle + 1;
            }
        }
        System.out.format("min = %d, distance = %d\n", min, distance(positions, min));
    }

    private static List<Long> readDataFile(String filename) throws FileNotFoundException {
        Scanner in = new Scanner(new File(filename));
        String line = in.nextLine();
        List<Long> positions = Arrays.asList(line.split(",")).stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
        System.out.println(positions);
        return positions;
    }

    private static long distance(List<Long> positions, long destination) {
        return positions.stream()
                .mapToLong(p -> Math.abs(p - destination))
                .sum();
    }

    private static void part2(String filename) throws FileNotFoundException {
        List<Long> positions = readDataFile(filename);

        var min = positions.stream().mapToLong(i -> i).min().getAsLong();
        var max = positions.stream().mapToLong(i -> i).max().getAsLong();

        while (min < max) {
            long middle = (min + max) / 2;

            long diff = distance2(positions, middle + 1) - distance2(positions, middle);
            
            if (diff > 0) {
                max = middle;
            } else {
                min = middle + 1;
            }
        }
        System.out.format("min = %d, distance = %d\n", min, distance2(positions, min));
    }

    private static long distance2(List<Long> positions, long destination) {
        return positions.stream()
                .mapToLong(p -> {
                    var steps = Math.abs(p - destination);
                    return (steps * (steps + 1)) / 2;
                })
                .sum();
    }
}
