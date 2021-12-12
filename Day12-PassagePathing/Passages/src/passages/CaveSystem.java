package passages;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CaveSystem {

    final Set<String> smallCaves = new HashSet<>();
    final Set<String> largeCaves = new HashSet<>();
    final Map<String, List<String>> destinations = new HashMap<>();

    void addCave(String c) {
        if (Character.isLowerCase(c.charAt(0))) {
            if (smallCaves.contains(c)) {
                return;
            } else {
                smallCaves.add(c);
            }
        } else {
            if (largeCaves.contains(c)) {
                return;
            } else {
                largeCaves.add(c);
            }
        }
        destinations.put(c, new ArrayList<>());
    }

    void addPassage(String c1, String c2) {
        addCave(c1);
        addCave(c2);
        destinations.get(c1).add(c2);
        destinations.get(c2).add(c1);
    }

    public CaveSystem(String filename) throws IOException {
        Files.lines(Paths.get(filename))
                .forEach(line -> {
                    var a = line.split("-");
                    addPassage(a[0], a[1]);
                });
    }

    public void forEachPath1(String extremity, Set<String> visitedSmallCaves,
            Runnable action) {
        if (extremity.equals("end")) {
            action.run();
        }
        for (var next : destinations.get(extremity)) {
            if (largeCaves.contains(next)) {
                forEachPath1(next, visitedSmallCaves, action);
            } else if (!visitedSmallCaves.contains(next)) {
                // a small cave, never visited yed
                visitedSmallCaves.add(next);
                forEachPath1(next, visitedSmallCaves, action);
                visitedSmallCaves.remove(next);
            }
        }
    }

    public void forEachPath2(String extremity,
            Set<String> visitedSmallCaves,
            boolean doubleVisitAllowed,
            Runnable action,
            String message) {

        if (extremity.equals("end")) {
//            System.out.println("= " + message + " (end)");
            action.run();
            return;
        }
        for (var next : destinations.get(extremity)) {
            if (next.equals("start")) {
                continue;
            }
            if (largeCaves.contains(next)) {
                // a large cave
                forEachPath2(next, visitedSmallCaves, doubleVisitAllowed, action,
                        message + " " + extremity);
            } else if (!visitedSmallCaves.contains(next)) {
                // a small cave, never visited yet
                visitedSmallCaves.add(next);
                forEachPath2(next, visitedSmallCaves, doubleVisitAllowed, action,
                        message + " " + extremity);
                visitedSmallCaves.remove(next);
            } else if (doubleVisitAllowed) {
                // a small cave, already visited, but allowed to visit again
                forEachPath2(next, visitedSmallCaves, false, action,
                        message + " " + extremity);
            }
        }
    }

    public int nbDistinctPaths1() {
        final int[] counter = {0}; // faking a mutable integer 
        var visitedSmallCaves = new HashSet<String>(List.of("start"));
        forEachPath1("start", visitedSmallCaves,
                () -> counter[0]++
        );
        return counter[0];
    }

    int nbDistinctPaths2() {
        final int[] counter = {0}; // faking a mutable integer

        var visitedSmallCaves = new HashSet<String>(List.of("start"));
        forEachPath2("start", visitedSmallCaves, true,
                () -> counter[0]++, "->"
        );
        return counter[0];
    }
}
