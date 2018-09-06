package Prover;

import static Prover.Mode.Algorithm.BREADTH;

public class Mode {
    public enum Algorithm {
        BREADTH("standard breadth-first"), MULTI_BREADTH("breadth-first, ignoring hue orderings within colour labels"), CONCURRENT_MULTI_BREADTH("multi-threaded breadth-first, ignoring hue orderings within colour labels"), CONCURRENT_BREADTH("multithreaded breadth-first"),
        BREADTH_PERMUTATIONS("breadth-first, checking all possible hue permutations");
        public String printString;
        Algorithm(String printString) {
            this.printString = printString;
        }
    }
    public static Algorithm setAlgorithm = BREADTH;
    public static boolean hueOrder = true;

    public static boolean xHues = false;
    //todo: checkmode method
}
