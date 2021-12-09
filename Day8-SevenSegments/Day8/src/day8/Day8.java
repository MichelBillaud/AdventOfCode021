package day8;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Day8 {

    private static List<Entry> readEntries(String filename) throws IOException {

        return Files.lines(Paths.get(filename))
                .map(line -> {
                    var parts = line.trim().split("\\|");
                    var left = parts[0].trim().split("\\s+");
                    var right = parts[1].trim().split("\\s+");
                    return new Entry(Arrays.asList(left), Arrays.asList(right));
                })
                .toList();
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {
        //test1("../small.txt");
        // test1("../large.txt");
        test2("../large.txt");
    }

    private static void test1(String filename) throws FileNotFoundException, IOException {
        List<Entry> entries = readEntries(filename);

        long count = entries.stream()
                .map(Entry::right)
                .flatMap(List::stream)
                .filter(Day8::isOneOf1478)
                .count();
        System.out.format("count on %s = %d\n", filename, count);
    }

    static boolean isOneOf1478(String s) {
        return switch (s.length()) {
            case 2, 3, 4, 7 ->
                true;
            default ->
                false;
        };
    }

    static int toBinaryMask(String s) {
        int mask = 0;
        for (var c : s.toCharArray()) {
            mask |= (1 << (c - 'a'));
        }
        // System.out.println("toBinaryMask " + s + " = " + r);
        return mask;
    }

    static int and(List<Integer> list) {
        return list.stream().reduce((x, y) -> x & y).get();
    }

    static int solve(Entry entry) {
        // convert display descriptions to bit masks
        // a=1, b=2, c=4, etc.
        int[] displayMasks = entry.left().stream()
                .mapToInt(Day8::toBinaryMask)
                .toArray();
        List<List<Integer>> displayMasksBySize = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            displayMasksBySize.add(new ArrayList<>());
        }
        for (var rep : displayMasks) {
            displayMasksBySize.get(Integer.bitCount(rep)).add(rep);
        }

        // Segment Mask have 1 bit set
        int[] segmentMasks = new int[10];
        segmentMasks[1] = displayMasksBySize.get(2).get(0);
        segmentMasks[7] = displayMasksBySize.get(3).get(0);
        segmentMasks[4] = displayMasksBySize.get(4).get(0);
        segmentMasks[8] = displayMasksBySize.get(7).get(0);

        int commonSegmentsFor235 = and(displayMasksBySize.get(5));
        int commonSegmentsFor069 = and(displayMasksBySize.get(6));
        int a = segmentMasks[7] & ~segmentMasks[1];
        int d = commonSegmentsFor235 & ~commonSegmentsFor069;
        int b = segmentMasks[4] & ~segmentMasks[1] & ~d;
        int f = commonSegmentsFor069 & segmentMasks[1];

        int c = segmentMasks[1] & ~f;
        int g = commonSegmentsFor235 & ~a & ~d;
        int e = segmentMasks[8] & ~segmentMasks[4] & ~commonSegmentsFor235;
  
        segmentMasks[0] = segmentMasks[8] & ~d;
        segmentMasks[2] = a | c | d | e | g;
        segmentMasks[3] = segmentMasks[7] | d | g;
        segmentMasks[5] = a | b | d | f | g;
        segmentMasks[6] = segmentMasks[8] & ~c;
        segmentMasks[9] = segmentMasks[8] & ~e;
        
        var displayMaskValue = new HashMap<Integer, Integer>();
        for (int i = 0; i <= 9; i++) {
            displayMaskValue.put(segmentMasks[i], i);
        }

        int sum = 0;
        for (var s : entry.right()) {
            sum = 10 * sum + displayMaskValue.get(toBinaryMask(s));
        }
        return sum;
    }

    private static void test2(String filename) throws FileNotFoundException, IOException {
        List<Entry> entries = readEntries(filename);
        int sum = entries.stream().mapToInt(Day8::solve).sum();
        System.out.println("Sum = " + sum);
    }

}
