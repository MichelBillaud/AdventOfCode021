package reactorreboot;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReactorReboot {

    static final Pattern LINE_PATTERN
            = Pattern.compile(
                    "(on|off)" + "\s"
                    + "x=(-?\\d+)\\.\\.(-?\\d+)" + ","
                    + "y=(-?\\d+)\\.\\.(-?\\d+)" + ","
                    + "z=(-?\\d+)\\.\\.(-?\\d+)"
            );

    public static void main(String[] args) throws IOException {

        //var puzzle = new ReactorReboot("../first.txt");
        //var puzzle = new ReactorReboot("../small.txt");
        //var puzzle = new ReactorReboot("../large.txt");

        /*
        var puzzle = new ReactorReboot("../second.txt");

        puzzle.part1();
        System.out.println("Expected for second : 2758514936282235");
        puzzle.part2();
         */
        var p2 = new ReactorReboot("../large.txt");
        p2.part2();

    }

    final String filename;
    final List<Step> steps;

    public ReactorReboot(String filename) throws IOException {
        this.filename = filename;
        steps = Files.lines(Paths.get(filename))
                .map(ReactorReboot::toStep)
                .toList();
    }

    static Step toStep(String line) {
        Matcher matcher = LINE_PATTERN.matcher(line);
        matcher.find();
        boolean turn_on = matcher.group(1).equals("on");
        int v[] = new int[6];
        for (int i = 0; i < 6; i++) {
            v[i] = Integer.parseInt(matcher.group(2 + i));
        }
        return new Step(turn_on,
                new Cuboid(v[0], v[2], v[4],
                        v[1], v[3], v[5]));
    }

    static record Point(int x, int y, int z) {

        Point(int v[]) {
            this(v[0], v[1], v[2]);
        }

    }

    static record Cuboid(int x1, int y1, int z1, int x2, int y2, int z2) {

        Optional<Cuboid> inter(Cuboid other) {

            var tmp = new Cuboid(
                    Math.max(x1, other.x1),
                    Math.max(y1, other.y1),
                    Math.max(z1, other.z1),
                    Math.min(x2, other.x2),
                    Math.min(y2, other.y2),
                    Math.min(z2, other.z2)
            );
            return tmp.isEmpty() ? Optional.empty() : Optional.of(tmp);
        }


        long volume() {
            return (x2 - x1 + 1L) * (y2 - y1 + 1L) * (z2 - z1 + 1L);
        }

        boolean isEmpty() {
            return (x1 > x2) || (y1 > y2) || (z1 > z2);
        }

        List<Cuboid> less(Cuboid other) {
            var r = new ArrayList<Cuboid>();
            inter(other).ifPresentOrElse(b -> {
                var a = this;

                if (a.x1 < b.x1) {
                    r.add(new Cuboid(a.x1, a.y1, a.z1, b.x1 - 1, a.y2, a.z2));
                    a = new Cuboid(b.x1, a.y1, a.z1, a.x2, a.y2, a.z2);
                }
                if (a.y1 < b.y1) {
                    r.add(new Cuboid(a.x1, a.y1, a.z1, a.x2, b.y1 - 1, a.z2));
                    a = new Cuboid(a.x1, b.y1, a.z1, a.x2, a.y2, a.z2);
                }
                if (a.z1 < b.z1) {
                    r.add(new Cuboid(a.x1, a.y1, a.z1, a.x2, a.y2, b.z1 - 1));
                    a = new Cuboid(a.x1, a.y1, b.z1, a.x2, a.y2, a.z2);
                }

                if (a.x2 > b.x2) {
                    r.add(new Cuboid(b.x2 + 1, a.y1, a.z1, a.x2, a.y2, a.z2));
                    a = new Cuboid(a.x1, a.y1, a.z1, b.x2, a.y2, a.z2);
                }
                if (a.y2 > b.y2) {
                    r.add(new Cuboid(a.x1, b.y2 + 1, a.z1, a.x2, a.y2, a.z2));
                    a = new Cuboid(a.x1, a.y1, a.z1, a.x2, b.y2, a.z2);
                }
                if (a.z2 > b.z2) {
                    r.add(new Cuboid(a.x1, a.y1, b.z2 + 1, a.x2, a.y2, a.z2));
                    a = new Cuboid(a.x1, a.y1, a.z1, a.x2, a.y2, b.z2);
                }
            }, () -> {
                r.add(this);
            });
            return r;
        }
    }

    static record Step(boolean turn_on, Cuboid cuboid) {

        private void applyTo(HashSet<Point> points) {
            for (int x = cuboid.x1; x <= cuboid.x2; x++) {
                for (int y = cuboid.y1; y <= cuboid.y2; y++) {
                    for (int z = cuboid.z1; z <= cuboid.z2; z++) {
                        final Point point = new Point(x, y, z);
                        if (turn_on) {
                            points.add(point);
                        } else {
                            points.remove(point);
                        }
                    }
                }
            }
        }

        private void applyTo(HashSet<Point> points, Cuboid area) {
            var xmin = Integer.max(area.x1, cuboid.x1);
            var ymin = Integer.max(area.y1, cuboid.y1);
            var zmin = Integer.max(area.z1, cuboid.z1);
            var xmax = Integer.min(area.x2, cuboid.x2);
            var ymax = Integer.min(area.y2, cuboid.y2);
            var zmax = Integer.min(area.z2, cuboid.z2);

            for (int x = xmin; x <= xmax; x++) {
                for (int y = ymin; y <= ymax; y++) {
                    for (int z = zmin; z <= zmax; z++) {
                        final Point point = new Point(x, y, z);
                        if (turn_on) {
                            points.add(point);
                        } else {
                            points.remove(point);
                        }
                    }
                }
            }
        }
    }

    private void part1() {
        var area = new Cuboid(-50, -50, -50, 50, 50, 50);
        var cubes = new HashSet<Point>();
        for (var step : steps) {
            step.applyTo(cubes, area);
        }
        System.out.format("# Part 1 '%s' has %d cubes.\n",
                filename, cubes.size());
    }

    private void part2() {
        var present = new ArrayList<Cuboid>();
        for (var step : steps) {
            var union = new ArrayList<Cuboid>();
            for (var c : present) {
                final List<Cuboid> differences = c.less(step.cuboid);
                union.addAll(differences);
            }
            present = union;
            if (step.turn_on) {
                present.add(step.cuboid);
            }
        }
        long totalSize = 0;
        for (var c : present) {
            totalSize += c.volume();
        }
        System.out.format("Part 2 : total size %d for %s\n ", 
                totalSize, filename);

    }
}
