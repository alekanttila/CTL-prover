package Prover.Prover;

import Prover.Formula.Formula;
import Prover.Formula.FormulaSet;

import java.util.*;

import static Prover.Prover.LG3.nH;

class PartialStructure extends Pair<List<Node>, Pair<Map<Node, TreeSet<FormulaSet>>, Map<Pair.NodeHue, Set<Pair.NodeHue>>>> {
    protected PartialStructure(List<Node> nodes, Map<Node, TreeSet<FormulaSet>> partialColouring, Map<NodeHue, Set<NodeHue>> hueSuccessors) {
        this.a = nodes;
        this.b = new Pair<>();
        this.b.a = partialColouring;
        this.b.b = hueSuccessors;
    }
    protected PartialStructure() {
        this.a = new ArrayList<Node>();
        this.b = new Pair<>();
        this.b.a = new HashMap<Node, TreeSet<FormulaSet>>();
        this.b.b = new HashMap<NodeHue, Set<NodeHue>>();
    }
    protected PartialStructure(PartialStructure previousPS) {
        this.a = previousPS.nodes();
        this.b = new Pair<>();
        this.b.a = new HashMap<Node, TreeSet<FormulaSet>>();
        this.b.b = new HashMap<NodeHue, Set<NodeHue>>();
        for (Node n : previousPS.nodes()) {
            this.resetColour(n);
        }
    }

    protected void simpleSuccessorUpdate(PartialStructure oldS, ExtensionData updatedHues) {
        initializeSuccessorSets();
        for (Node n1 : oldS.nodes()) {
            for (FormulaSet h1 : oldS.pC().get(n1)) {
                for (Node n2 : oldS.nodes()) {
                    for (FormulaSet h2 : oldS.pC().get(n2)) {
                        if (oldS.isSuccessor(n1, h1, n2, h2)) {
                            addSuccessor(updatedHues, n1, h1, updatedHues, n2, h2);
                        }
                    }
                }
            }
        }
    }

    //TODO: add these methods into extensiondata/partialstructure
    protected boolean isSuccessor(Node n1, FormulaSet h1, Node n2, FormulaSet h2) {
        boolean result;
        if (hS().get((nH(n1,h1))) == null) {
            result = false;
        } else {
            result = hS().get(nH(n1, h1)).contains(nH(n2, h2));
        }
        return result;
    }

    //TODO: make this part of partialstructure
    protected void resetColour(Node n) {
        //TODO: check; write that this does not work unless i is the next colour to be added in sequence
        pC().put(n, new TreeSet<FormulaSet>());
    }

    //TODOL:add as part of partialstructure
    //TODO: improve this with checks form old LG calls?
    protected void initialize(Node n) {
        if (!nodes().contains(n)) {
            TreeSet<FormulaSet> startColour = new TreeSet<FormulaSet>();
            startColour.add(LG3.getEmptyHue());
            pC().put(n, startColour);
            nodes().add(n);
            hS().put(nH(n, LG3.getEmptyHue()), new HashSet<NodeHue>());
            Iterator<Node> i = n.successors.values().iterator();
            while (i.hasNext()) {
                Node s = i.next();
                initialize(s);
                //TODO: change this
                hS().get(nH(n, LG3.getEmptyHue())).add(nH(s, LG3.getEmptyHue()));
            }
            //TODO: mention lemma 3.1 in relation to node traversal
        }
    }

    protected List<Node> nodes() {
        return this.a;
    }
    protected Map<Node, TreeSet<FormulaSet>> pC() {
        return this.b.a;
    }
    protected Map<NodeHue, Set<NodeHue>> hS() {
        return this.b.b;
    }
    protected TreeSet<FormulaSet> getNodeColour(Node n) {
        return this.b.a.get(n);
    }
    protected void addHueToNodeColour(Node n, FormulaSet h) {
        this.b.a.get(n).add(h);
        //if (colour == null) {
            //TODO: check if this is actually working!!!!
            //colour = new TreeSet<FormulaSet>();
            //colour = this.b.a.put(n, new TreeSet<FormulaSet>());
        //}
    }

    protected void addHue(Node n, FormulaSet oldH, Formula newF, ExtensionData e) {
        FormulaSet newH = new FormulaSet();
        newH.addAll(oldH);
        if (newF != null) {
            newH.add(newF);
        }
        this.addHueToNodeColour(n, newH);
        e.addHue(n, oldH, newH);
    }

    protected void initializeSuccessorSets() {
        for (Node n : nodes()) {
            for (FormulaSet h : pC().get(n)) {
                hS().put(nH(n, h), new HashSet<NodeHue>());
            }
        }
    }

    protected void addSuccessor(ExtensionData e1, Node n1, FormulaSet h1, ExtensionData e2, Node n2, FormulaSet h2) {
        Set<NodeHue> successorNodeHues = this.hS().get(nH(n1, e1.getHueMap().get(nH(n1, h1))));
        //if (successorNodeHues == null) {
            //TODO: see above in addtonodecolour
            //this.hS().put(nH(n1, e1.getHueMap().get(nH(n1,h1))), new HashSet<LG3.NodeHue>());
            //successorNodeHues = this.hS().get(nH(n1, e1.getHueMap().get(nH(n1, h1))));
        //}
        successorNodeHues.add(nH(n2, e2.getHueMap().get(nH(n2, h2))));
    }
    public String sugarString(Map<Formula, String> formulaNames) {
        String result = "";
        for (Node n : this.nodes()) {
            result = result + "Node " + n.getName() + "\n";
            for (FormulaSet h : this.getNodeColour(n)) {
                result = result + "  Hue ";
                result = result + h.sugarString() + "\n";
                result = result + "  with successors:\n";
                Set<NodeHue> successors =  this.hS().get(nH(n,h));
                Iterator<NodeHue> i = successors.iterator();
                while (i.hasNext()) {
                    NodeHue s = i.next();
                    result = result + "  " + s.a.getName() + " " + s.b.sugarString(formulaNames) + "\n";
                }
            }
        }
        return result;
    }

    protected static class ExtensionData extends Pair<Map<NodeHue, FormulaSet>, Map<NodeHue, Boolean>> {
        protected ExtensionData() {
            this.a = new HashMap<NodeHue, FormulaSet>();
            this.b = new HashMap<NodeHue, Boolean>();
        }
        protected void setBoolean(Node n, FormulaSet h, Boolean b) {
            this.b.put(nH(n,h), b);
        }
        protected boolean getBoolean(Node n, FormulaSet h) {
            //TODO: write in report, cost savings
            //all the algorithms start by making these false; we can assume that an empty entry equals false
            Boolean result = this.b.get((nH(n,h)));
            if (result == null) {
                result = false;
            }
            return result;
        }
        protected void addHue(Node n, FormulaSet h, FormulaSet newH) {
            this.a.put(nH(n,h), newH);
        }
        //TODO: remove this and refactor?
        protected Map<NodeHue, FormulaSet> getHueMap() {
            return this.a;
        }
    }
}
