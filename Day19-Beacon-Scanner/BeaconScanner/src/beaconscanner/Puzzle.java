package beaconscanner;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Stack;

/**
 *
 * @author billaud
 */
public class Puzzle {

    ArrayList<ScannerInfo> maps = new ArrayList<>();
    final String filename;

    public Puzzle(String filename) throws IOException {
        try (Scanner s = new Scanner(Paths.get(filename))) {
            while (s.hasNextLine()) {
                maps.add(read_map(s));
            }
        }
        this.filename = filename;
    }

    public void display() {
        maps.forEach(ScannerInfo::display);
    }

    private ScannerInfo read_map(Scanner s) {
        var title = s.nextLine();
        var positions = new ArrayList<Vector>();
        // System.out.println("Title : " + title);
        while (s.hasNextLine()) {
            String line = s.nextLine();
            String[] c = line.split(",");
            if (c.length == 3) {
                int[] array = Arrays.asList(c).stream()
                        .mapToInt(Integer::parseInt)
                        .toArray();
                positions.add(new Vector(array));
            } else {
                break;
            }
        }
        return new ScannerInfo(title, positions);
    }

    void solve() {
        var others = new HashSet<>(maps);
        var todo = new Stack<ScannerInfo>();
        ScannerInfo first = maps.get(0);
        first.setRelativePosition(Vector.ZERO);             // PART 2

        others.remove(first);
        todo.push(first);
        while (!todo.empty()) {
            var a = todo.pop();
            var found = new ArrayList<ScannerInfo>();
            for (var b : others) {
                for (var r : Rotation.ROTATIONS) {
                    var m = r.find_matching(a, b);
                    if (m.isPresent()) {
                        //System.out.format("%s -> %s  rotation %s translation %s \n",
                        //        a.title, b.title, r, m.get());
                        b.rotate(r);
                        b.translate(m.get());
                        found.add(b);
                        todo.push(b);
                        // for part 2
                        b.setRelativePosition(m.get());     // PART 2
                        break;
                    }
                }
            }
            others.removeAll(found);
        }
        // cumulate
        var beacons = new HashSet<Vector>();
        for (var m : maps) {
            beacons.addAll(m.positions);
        }
        System.out.format("Part 1 file '%s' total %d beacons.\n",
                filename, beacons.size());

        int max = 0;
        for (var a : maps) {
            // System.out.format("%s is at %s\n", a.title, a.relativePosition);
            for (var b : maps) {
                int distance = a.getRelativePosition()
                        .difference(b.getRelativePosition()).norm();
                if (distance > max) {
                    max = distance;
                }
            }
        }
        System.out.format("Part 2 max Manhattan distance is %d\n", max);

    }
}
