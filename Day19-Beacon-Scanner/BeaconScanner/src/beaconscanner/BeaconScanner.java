package beaconscanner;

import java.io.IOException;
import java.util.Optional;

public class BeaconScanner {

    public static void main(String[] args) throws IOException {
        var small = new Puzzle("../small.txt");
        small.solve();
        var large = new Puzzle("../large.txt");
        large.solve();

    }
   
}
