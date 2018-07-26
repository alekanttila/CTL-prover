package Prover.Prover;

import Prover.Formula.Formula;
import Prover.Formula.FormulaSet;

import java.util.*;

import static Prover.Formula.Formula.Connective.NOT;

/*
public class LG2 {
    //for code legibility
    private static class NodeHue extends Pair<Node, FormulaSet> {
        private NodeHue(Node n, FormulaSet h) {
            this.a = n;
            this.b = h;
        }
        private Node node() {
            return this.a;
        }
        private FormulaSet hue() {
            return this.b;
        }
    }

    private static class PartialStructureNode extends Pair<Node, Pair<TreeSet<FormulaSet>, boolean[]>> {

    }

    //could construct the algorithm by recursing down the tree, but since the
    // tree loops back on itself, this would necessitate traversal checks
    //whenever any node is update; we use a single list of nodes instead
    //(and do the traversal check only in the initialization phase)
    //stored statically to avoid returning both of these in all methods;
    //we still have to pass them to make use of old values as we go
    static Formula f;

    public static boolean check(Formula phi, Node root) {
        //reset static members
        f = phi;
        FormulaSet closure = f.getClosure();
        PartialStructureNode[] currentStructure = initialize(root, new PartialStructureMapList());

        for (Formula g : closure) {
            switch (g.getC()) {
                case ATOM:
                    updateAtom(g, currentStructure);
                    break;
            }

        }

        return false;
    }

    private static void updateAtom(Formula atom, PartialStructureMapList oldS) {
        PartialStructureMapList newS = new PartialStructureMapList();
        for (Node n : oldS.nodes()) {
            //initialize new partial colour:
            resetColour(n, newS);
            Formula newF;
            boolean add = n.z.containsF(atom);
            if (add) {
                newF = atom;
            } else {
                newF = new Formula(atom, NOT);
            }
            for (FormulaSet h : oldS.pC().get(n)) {
                FormulaSet newH = new FormulaSet();
                newH.addAll(h);
                newH.add(newF);
                newS.pC().get(n).add(newH);
            }
        }
        for (Node n1 : oldS.nodes()) {
            for (FormulaSet h1 : oldS.pC().get(n1)) {
                for (Node n2 : oldS.nodes()) {
                    for (FormulaSet h2 : oldS.pC().get(n2)) {
                        if (oldS.hS().)
                    }
                }

            }
        }


    }



    //TODO: improve this with checks form old LG calls?
    private static PartialStructureMapList initialize(Node n, PartialStructureMapList pS) {
        if (!pS.nodes().contains(n)) {
            resetColour(n, pS);
            pS.nodes().add(n);
            for (int i = 0; i < n.successors.size(); i++) {
                Node s = n.successors.get(i);
                initialize(s, pS);
                pS.hS().put(new NodeHue(n, getEmptyHue()), new NodeHue(s, getEmptyHue()));
            }
            //TODO: mention lemma 3.1 in relation to node traversal
        }
        return pS;
    }

    private static FormulaSet getEmptyHue() {
        FormulaSet empty = new FormulaSet();
        return empty;
    }

    private static TreeSet<FormulaSet> getEmptyColour() {
        TreeSet<FormulaSet> onlyEmpty = new TreeSet<FormulaSet>();
        onlyEmpty.add(getEmptyHue());
        return onlyEmpty;
    }

    private static void resetColour(Node n, PartialStructureMapList pS) {
        pS.pC().put(n, getEmptyColour());
    }
}
*/