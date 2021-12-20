package beaconscanner;

import beaconscanner.Vector.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author billaud
 */
public class Rotation {

    String description;
    int[][] mat;

    public Rotation(String description, int[][] mat) {
        this.description = description;
        this.mat = mat;
    }

    public Rotation product(Rotation other) {
        int[][] m = new int[3][3];
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                for (int k = 0; k < 3; k++) {
                    m[r][c] += mat[r][k] * other.mat[k][c];
                }
            }
        }
        return new Rotation(description + other.description, m);
    }

    public Vector apply(Vector vector) {
        int[] v = {0, 0, 0};
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                v[r] += mat[r][c] * vector.array[c];
            }
        }
        return new Vector(v);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Arrays.deepHashCode(this.mat);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Rotation other = (Rotation) obj;
        return Arrays.deepEquals(this.mat, other.mat);
    }

    public static ArrayList<Rotation> ROTATIONS;

    static {
        // generate the 24 rotations
        int[][] RX = {{1, 0, 0}, {0, 0, -1}, {0, 1, 0}};
        int[][] RY = {{0, 0, 1}, {0, 1, 0}, {-1, 0, 0}};
        int[][] RZ = {{0, 1, 0}, {-1, 0, 0}, {0, 0, 1}};

        ArrayList<Rotation> list = new ArrayList<>();
        list.add(new Rotation("x", RX));
        list.add(new Rotation("y", RY));
        list.add(new Rotation("z", RZ));
        HashSet<Rotation> set = new HashSet<>(list);
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j <= i; j++) {
                var p = list.get(i).product(list.get(j));
                if (!set.contains(p)) {
                    // System.out.println("adding " + p.description);
                    list.add(p);
                    set.add(p);
                }
            }
        }
        ROTATIONS = list;
    }

    void test_matching(ScannerInfo a, ScannerInfo b) {
        var matchings = new HashMap<Vector, HashMap<Vector, Vector>>();

        for (var vb : b.positions) {
            Vector imb = apply(vb);

            for (Vector va  : a.positions) {
                var diff = va.difference(imb);
                matchings.putIfAbsent(diff, new HashMap<>());
                matchings.get(diff).put(va, vb);
            }
        }

        matchings.entrySet().stream()
                .max(Comparator.comparing(e -> e.getValue().size()))
                .map(e -> e.getValue().size())
                .filter(s -> s >= 12)
                .ifPresentOrElse(n -> {
                System.out.format("Found %d matchings\n", n );},
                        () ->{System.out.println("Fails");});
                        
                        
    }
    
    Optional<Vector> find_matching(ScannerInfo a, ScannerInfo b) {
        var matchings = new HashMap<Vector, HashMap<Vector, Vector>>();

        for (var vb : b.positions) {
            Vector imb = apply(vb);

            for (Vector va  : a.positions) {
                var diff = va.difference(imb);
                matchings.putIfAbsent(diff, new HashMap<>());
                matchings.get(diff).put(va, vb);
            }
        }
        return matchings.entrySet().stream()
                .filter(e -> e.getValue().size() >= 12)
                .findAny()
                .map(Entry::getKey);
    }

    @Override
    public String toString() {
        return "R" + description;
    }
    
    
}
