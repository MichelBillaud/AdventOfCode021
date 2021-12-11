package vents;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Vents {

    static final Pattern linePattern = Pattern.compile(
                 "(\\d+),(\\d+) -> (\\d+),(\\d+)"    
            
    );
    
    static int [] parseLine(String line) {
           var matcher = linePattern.matcher(line.trim());
           if (matcher.matches()) {
               int[] array  = {
                   Integer.parseInt(matcher.group(1)),
                   Integer.parseInt(matcher.group(2)),
                   Integer.parseInt(matcher.group(3)),
                   Integer.parseInt(matcher.group(4))
               };
               return array;
           }
           return null;
    }

    public static void main(String[] args) {
        try {
            //part1("../small.txt");
            //part1("../large.txt");
            // part2("../small.txt");
            part2("../large.txt");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Vents.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static List<Line> readLines(Scanner in) {
        var l = new ArrayList<Line>();
        while (in.hasNextLine()) {
            var array = parseLine(in.nextLine().trim());
            if (array != null) {
                l.add(new Line(array));
            }
        }
        return l;
    }

    private static void part1(String filename)
            throws FileNotFoundException {
        var in = new Scanner(new File(filename));
        var lines = readLines(in);
        long count = lines.stream()
                .filter(Line::isHorizontalOrVertical)
                .flatMap(Line::allPositions)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() >= 2)
                .count();
        System.out.format("%d points in %s\n",
                count, filename);
    }

    private static void part2(String filename)
            throws FileNotFoundException {
        var in = new Scanner(new File(filename));
        var lines = readLines(in);
        long count = lines.stream()
                .flatMap(Line::allPositions)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()))
                .entrySet()
                .stream()
                .filter(e -> e.getValue() >= 2)
                .count();
        System.out.format("%d points in %s\n",
                count, filename);
    }
}
