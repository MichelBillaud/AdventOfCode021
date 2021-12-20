package beaconscanner;

import static java.lang.Math.abs;
import java.util.Arrays;

public class Vector {

    public static record Pair(Vector p1, Vector p2) {

    }

    final int[] array;

    public Vector(int[] array) {
        this.array = array.clone();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 17 * hash + Arrays.hashCode(this.array);
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
        final Vector other = (Vector) obj;
        return Arrays.equals(this.array, other.array);
    }

    public Vector difference(Vector other) {
        int[] d = new int[3];
        for (int i = 0; i < 3; i++) {
            d[i] = array[i] - other.array[i];
        }
        return new Vector(d);
    }

    public Vector plus(Vector other) {
        int[] d = new int[3];
        for (int i = 0; i < 3; i++) {
            d[i] = array[i] + other.array[i];
        }
        return new Vector(d);
    }

    // for part 2
    static Vector ZERO;

    static {
        int[] a = {0, 0, 0};
        ZERO = new Vector(a);
    }

    @Override
    public String toString() {
        return Arrays.toString(array);
    }

    int norm() {
        int s = 0;
        for (int i = 0; i < 3; i++) {
            s += abs(array[i]);
        }
        return s;
    }

}
