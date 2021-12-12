package passages;

import java.io.IOException;

/**
 *
 * @author billaud
 */
public class Passages {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        testConstruction("../test10.txt");

        processFile("../test10.txt");
        
        processFile("../test19.txt");
        processFile("../test226.txt");
        processFile("../large.txt");

    }

    static void testConstruction(String filename) throws IOException {
        System.out.format("Loading from '%s': \n", filename);
        var s = new CaveSystem(filename);
        System.out.println("small := " + s.smallCaves);
        System.out.println("large := " + s.largeCaves);
        System.out.println("---");
        s.destinations.forEach((src, dst) -> {
            System.out.format("%s -> %s\n", src, dst);
        });
        System.out.println("---");
        System.out.println();
    }

    static void processFile(String filename) throws IOException {
        var s = new CaveSystem(filename);

        System.out.format("File '%s':\n", filename);
        System.out.format("- Part 1, %6d distinct paths found\n",
                s.nbDistinctPaths1());

        System.out.format("- Part 2, %6d distinct paths found\n",
                s.nbDistinctPaths2());
        
        System.out.println("");

    }

}
