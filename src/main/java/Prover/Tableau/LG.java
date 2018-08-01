package Prover.Tableau;

/*
public class LG {
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

    private static class HueSuccessors extends ArrayList<Pair<Integer, Integer>> {
    }

    private static class PartialStructureList extends Pair<List<Node>, Pair<List<TreeSet<FormulaSet>>, HueSuccessors>> {
        private PartialStructureList(List<Node> nodes, List<TreeSet<FormulaSet>> partialColouring, HueSuccessors hueSuccessors) {
            this.a = nodes;
            this.b.a = partialColouring;
            this.b.b = hueSuccessors;
        }
        private PartialStructureList() {
            this.a = new ArrayList<Node>();
            this.b.a = new ArrayList<TreeSet<FormulaSet>>();
            this.b.b = new HueSuccessors();
        }
        private List<Node> nodes() {return this.a;}
        private List<TreeSet<FormulaSet>> pC() {return this.b.a;}
        private HueSuccessors hS() {return this.b.b;}
        private void sethS(HueSuccessors hS) {
            this.b.b = hS;
        }
    }

    private static class PartialStructureNode extends Pair<Node, Pair<TreeSet<FormulaSet>, boolean[]>> {

    }

    private static class PartialStructureMap extends Pair<List<Node>, Pair<Map<Node, TreeSet<FormulaSet>>, Map<NodeHue, NodeHue>>> {
        private PartialStructureMap(List<Node> nodes, Map<Node, TreeSet<FormulaSet>> partialColouring, Map<NodeHue, NodeHue> hueSuccessors) {
            this.a = nodes;
            this.b.a = partialColouring;
            this.b.b = hueSuccessors;
        }
        private PartialStructureMap() {
            this.a = new ArrayList<Node>();
            this.b.a = new HashMap<Node, TreeSet<FormulaSet>>();
            this.b.b = new HashMap<NodeHue, NodeHue>();
        }
        private List<Node> nodes() {
            return this.a;
        }
        private Map<Node, TreeSet<FormulaSet>> pC() {
            return this.b.a;
        }
        private Map<NodeHue, NodeHue> hS() {
            return this.b.b;
        }
        private TreeSet<FormulaSet> getNodeColour(Node n) {
            return this.b.a.get(n);
        }
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
        PartialStructureList currentStructure = initialize(root, new PartialStructureList());

        for (Formula g : closure) {
            switch (g.getC()) {
                case ATOM:
                    updateAtom(g, currentStructure);
                    break;
            }

        }

        return false;
    }

    private static void updateAtom(Formula atom, PartialStructureList oldS) {
        PartialStructureList newS = new PartialStructureList();
        //TODO look up arraylist, hashmap retrieval complexity
        //TODO: look up getindex complexity; change resetCOlour?
        //use one index for both nodes and hues so we do not have to use indexOf (or hashmap lookup)
        for (int i = 0; i < oldS.nodes().size(); i++) {
            Node n = oldS.nodes().get(i);
            //initialize new partial colour:
            resetColour(i, newS);
            Formula newF;
            boolean add = n.z.containsF(atom);
            if (add) {
                newF = atom;
            } else {
                newF = new Formula(atom, NOT);
            }
            for (FormulaSet h : oldS.pC().get(i)) {
                FormulaSet newH = new FormulaSet();
                newH.addAll(h);
                newH.add(newF);
                newS.pC().get(i).add(newH);
            }
        }
        /*
        for (Node n1 : oldS.nodes()) {
            for (FormulaSet h1 : oldS.pC().get(n1)) {
                for (Node n2 : oldS.nodes()) {
                    for (FormulaSet h2 : oldS.pC().get(n2)) {
                        if (oldS.hS().)
                    }
                }

            }
        }
        newS.sethS(oldS.hS());
    }

    //TODO: write correction in report
    private static void updateNot(Formula f, PartialStructureList oldS) {
        PartialStructureList newS = new PartialStructureList();
        for (int i = 0; i < oldS.nodes().size(); i++) {
            Node n = oldS.nodes().get(i);
            resetColour(i, newS);
            Formula newF;
            boolean add = n.z.containsF(f);
            if (add) {
                newF = atom;
            } else {
                newF = new Formula(atom, NOT);
            }
            for (FormulaSet h : oldS.pC().get(i)) {
                FormulaSet newH = new FormulaSet();
                newH.addAll(h);
                newH.add(newF);
                newS.pC().get(i).add(newH);
            }
        }
        /*
        for (Node n1 : oldS.nodes()) {
            for (FormulaSet h1 : oldS.pC().get(n1)) {
                for (Node n2 : oldS.nodes()) {
                    for (FormulaSet h2 : oldS.pC().get(n2)) {
                        if (oldS.hS().)
                    }
                }

            }
        }
        newS.sethS(oldS.hS());
    }



    private static PartialStructureList initialize(Node n, PartialStructureList pS) {
        return initializeAux(n, 0, pS);
    }

    //TODO: improve this with checks form old LG calls?
    private static PartialStructureList initializeAux(Node n, int counter, PartialStructureList pS) {
        if (!pS.nodes().contains(n)) {
            resetColour(counter, pS);
            pS.nodes().add(n);
            int newCounter = counter + 1;
            for (int i = 0; i < n.successors.size(); i++) {
                Node s = n.successors.get(i);
                initializeAux(s, newCounter, pS);
                pS.hS().add(new Pair(counter, newCounter));
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

    private static void resetColour(int i, PartialStructureList pS) {
        //TODO: check; write that this does not work unless i is the next colour to be added in sequence
        pS.pC().add(i, getEmptyColour());
    }
}
*/
