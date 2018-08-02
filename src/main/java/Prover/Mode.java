package Prover;

import static Prover.Mode.Algorithm.BREADTH;

public class Mode {
    public enum Algorithm {
        BREADTH("standard breadth first"), BREADTH_FIRST_HUE("breadth first, checking all possible first hues"),
        BREADH_PERMUTATIONS("breadth first, checking all possible hue permutations"), DEPTH("standard depth first");
        public String printString;
        Algorithm(String printString) {
            this.printString = printString;
        }
    }
    public static Algorithm setAlgorithm = BREADTH;

    public static boolean xHues = true;
    //todo: checkmode method
}
