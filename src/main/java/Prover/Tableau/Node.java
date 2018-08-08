package Prover.Tableau;

import Prover.Formula.Colour;
import Prover.Formula.Hue;
import Prover.Tableau.Pair.NodeHue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static Prover.Tableau.Pair.nH;

class Node {
    protected final Map<Integer, NodeHue> successors;
    protected final Colour z;
    protected final List<Hue> zOrder;
    protected final List<Node> ancestors;
    protected final int branchLength;
    private final String name;

    //standard node creation
    //TODO: add check for firsthue(is it in c)

    //for copying; parent can be null!
    protected Node(Node n, Node parent) {
        this.z = n.z;
        this.zOrder = n.zOrder;
        this.branchLength = n.branchLength;
        this.name = n.name;
        this.ancestors = new ArrayList<>();
        if (parent != null) {
            this.ancestors.addAll(parent.ancestors);
        }
        this.ancestors.add(this);
        this.successors = new HashMap<Integer, NodeHue>();
    }
    protected Node(Colour c, Hue firstHue, Node parent, String name) {
        this.z = c;
        this.zOrder = new ArrayList<Hue>();
        this.zOrder.add(0, firstHue);
        for (Hue hue : c) {
            //TODO: change to ==? and ensure beforehand we only use copies
            //whether or not equals results in extra computation depends on implementation in set
            if (!firstHue.equals(hue)) {
                zOrder.add(hue);
            }
        }
        this.ancestors = new ArrayList<Node>(parent.ancestors);
        this.ancestors.add(this);
        this.branchLength = parent.branchLength + 1;
        this.name = parent.name + name;
        this.successors = new HashMap<Integer, NodeHue>();
    }

    protected Node(Colour c, Map<Integer, Hue> hueOrder, Node parent, String name) {
        this.z = c;
        this.zOrder = new ArrayList<Hue>();
        for (int i = 0; i < c.size(); i ++) {
            zOrder.add(hueOrder.get(i));
        }
        this.ancestors = new ArrayList<Node>(parent.ancestors);
        this.ancestors.add(this);
        this.branchLength = parent.branchLength + 1;
        this.name = parent.name + name;
        this.successors = new HashMap<Integer, NodeHue>();
    }

    protected Node(Colour c, Map<Integer, Hue> hueOrder) {
        this.z = c;
        this.zOrder = new ArrayList<Hue>();
        for (int i = 0; i < c.size(); i ++) {
            zOrder.add(hueOrder.get(i));
        }
        this.ancestors = new ArrayList<>();
        this.ancestors.add(this);
        this.branchLength = 1;
        this.name = "r";
        this.successors = new HashMap<Integer, NodeHue>();
    }

    protected Node(Colour c, Hue firstHue) {
        this.z = c;
        this.zOrder = new ArrayList<Hue>();
        this.zOrder.add(0, firstHue);
        for (Hue hue : c) {
            //TODO: change to ==? and ensure beforehand we only use copies
            //whether or not equals results in extra computation depends on implementation in set
            if (!firstHue.equals(hue)) {
                zOrder.add(hue);
            }
        }
        this.ancestors = new ArrayList<>();
        this.ancestors.add(this);
        this.branchLength = 1;
        this.name = "r";
        this.successors = new HashMap<Integer, NodeHue>();
    }

    protected boolean isLeaf() {
        return (this.successors.isEmpty());
    }

    protected NodeHue getStandardNH() {
        return nH(this, this.zOrder.get(0));
    }

    protected String getName() {
        return this.name;
    }


    /*
    protected static Node copyTree(Node n, Node parent) {
        Node newNode = new Node(n, parent);
        for (int i = 0; i < n.successors.size(); i++) {
            Node s = (copyTree(n.successors.get(i).node(), newNode));
            newNode.successors.put(i, s);
        }
        return newNode;
    }*/
}
