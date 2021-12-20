package beaconscanner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ScannerInfo {

    String title;
    ArrayList<Vector> positions;

    public ScannerInfo(String title, ArrayList<Vector> positions) {
        this.title = title;
        this.positions = positions;
    }

    public void display() {
        System.out.println(title);
        positions.stream()
                .map(p -> Arrays.toString(p.array))
                .forEach(System.out::println);
        System.out.println("");
    }

    void rotate(Rotation r) {
        positions = positions.stream().map(r::apply)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    void translate(Vector v) {
        positions = positions.stream().map(v::plus)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    // for part 2
    Vector relativePosition;

    public Vector getRelativePosition() {
        return relativePosition;
    }

    public void setRelativePosition(Vector relativePosition) {
        this.relativePosition = relativePosition;
    }

}
