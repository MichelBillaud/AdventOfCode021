package day3;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Day3 {

    static int value(int[] t) {
        int r = 0;
        for (var x : t) {
            r += r + x;
        }
        return r;
    }

    static int value(String s) {
        return Integer.parseInt(s, 2);
    }

    static class Accumulator {

        final int[] nbZeroes;
        final int[] nbOnes;
        final int size;
        int counter = 0;

        public Accumulator(int size) {
            this.size = size;
            this.nbZeroes = new int[size];
            this.nbOnes = new int[size];
        }

        void updateWith(String data) {
            this.counter += 1;
            for (int i = 0; i < this.size; i++) {
                if (data.charAt(i) == '0') {
                    this.nbZeroes[i] += 1;
                } else {
                    this.nbOnes[i] += 1;
                }
            }
        }

        int[] mostCommon() {
            var a = new int[size];
            for (int i = 0; i < size; i++) {
                if (this.nbZeroes[i] < this.counter / 2) {
                    a[i] = 1;
                }
            }
            return a;
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append(String.format("counter = %d, zeroes =  [ ", counter));
            for (var v : nbZeroes) {
                builder.append(String.format("%d ", v));
            }
            var m = mostCommon();
            builder.append("], most common = ");
            for (var v : m) {
                builder.append(v);
            }
            var gamma = value(m);
            builder.append("\ngamma = ").append(gamma);

            var epsilon = (1 << size) - 1 - gamma;
            builder.append(", epsilon = ").append(epsilon);

            builder.append(", product = ").append(gamma * epsilon);

            return builder.toString();
        }
    }

    public static void main(String[] args) {
        // part1("large.txt", 12);    
        part2("small.txt");
        part2("large.txt");
    }

    private static void part1(String filename, int nbBits) {
        Accumulator accumulator;
        accumulator = new Accumulator(nbBits);

        try {
            Files.lines(Paths.get(filename))
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .forEach(accumulator::updateWith);
            System.out.println(accumulator);
        } catch (IOException ex) {
            Logger.getLogger(Day3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void part2(String filename) {
        System.out.println("## Part 2 with file " + filename);
        try {
            var data = Files.lines(Paths.get(filename))
                    .map(String::trim)
                    .filter(Predicate.not(String::isEmpty))
                    .collect(Collectors.toList());
            int nbBits = data.get(0).length();

            // oxygen
            // pour i de 0 à nbBits-1
            //    si un seul dans la liste, fini
            //    compter les bits de position i
            //    determiner la valeur majoritaire ; 1 si egalité
            //    filtrer ceux qui ont ce bit 
            var oxygen = data;

            for (int i = 0; i < nbBits; i++) {

                if (oxygen.size() == 1) {
                    break;
                }
                final var position = i;
                var list0 = oxygen.stream()
                        .filter(s -> s.charAt(position) == '0')
                        .collect(Collectors.toList());

                var list1 = oxygen.stream()
                        .filter(s -> s.charAt(position) == '1')
                        .collect(Collectors.toList());

                oxygen = list0.size() > list1.size()
                        ? list0 : list1;
            }
            System.out.format("oxygen = ", oxygen.get(0));
            var oxygenRating = value(oxygen.get(0));

            var co2 = data;
            for (int i = 0; i < nbBits; i++) {
                if (co2.size() == 1) {
                    break;
                }

                final var position = i;
                var list0 = co2.stream()
                        .filter(s -> s.charAt(position) == '0')
                        .collect(Collectors.toList());

                var list1 = co2.stream()
                        .filter(s -> s.charAt(position) == '1')
                        .collect(Collectors.toList());

                co2 = list0.size() <= list1.size()
                        ? list0 : list1;
            }
            var co2Rating = value(co2.get(0));
            System.out.format("co2 = %s\n", co2.get(0));
            System.out.format("result = %d * %d = %d\n",
                    oxygenRating, co2Rating, oxygenRating * co2Rating);

        } catch (IOException ex) {
            Logger.getLogger(Day3.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
