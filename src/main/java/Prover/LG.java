package Prover;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import Prover.Tableau.Node;

public class LG {
    static Map<Node, TreeSet<PartialHue>> partialColouring;
    static Map<Pair<Node, PartialHue>, Pair<Node, PartialHue>> hueSuccessors;

    private class hueSuccessorMap extends HashMap<Pair<Node, PartialHue>, Pair<Node, PartialHue>> {

    }

    public static boolean check(Formula f, Node root) {
        FormulaSet closure = f.getClosure();
        partialColouring = new HashMap<Node, TreeSet<PartialHue>>();
        for (Node n : root)

        return false;
    }

    void initi
}
