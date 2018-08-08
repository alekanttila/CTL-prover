package Prover;

import static Prover.Mode.Algorithm.BREADTH;
import static Prover.Mode.Algorithm.MULTI_BREADTH;

public class Mode {
    public enum Algorithm {
        BREADTH("standard breadth-first"), MULTI_BREADTH("multithreaded breadth-first"), BREADTH_FIRST_HUE("breadth-first, checking all possible first hues"),
        BREADTH_PERMUTATIONS("breadth-first, checking all possible hue permutations"), DEPTH("standard depth-first");
        public String printString;
        Algorithm(String printString) {
            this.printString = printString;
        }
    }
    public static Algorithm setAlgorithm = MULTI_BREADTH;
    public static boolean hueOrder = true;

    public static boolean xHues = true;
    //todo: checkmode method
}
