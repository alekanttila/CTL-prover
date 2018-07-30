package Prover.Prover;

import Prover.Formula.Colour;
import Prover.Formula.Hue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Node {
    protected Map<Integer, Node> successors;
    //note: must order colour!
    protected final Colour z;
    protected final List<Hue> zOrder;
    protected final List<Node> ancestors;
    protected final int branchLength;
    private final String name;

    //standard node creation
    //TODO: add check for firsthue(is it in c)
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
        this.successors = new HashMap<Integer, Node>();
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
        this.successors = new HashMap<Integer, Node>();
    }

    protected boolean isLeaf() {
        return (this.successors.isEmpty());
    }

    protected String getName() {
        return this.name;
    }
   /*
    //custom node creation
    protected Node(Colour z, Hue[] zT, List<Node> a, Node[] s) {
        this.z = z;
        this.zOrder = zT;
        this.ancestors = a;
        this.successors = s;
    }
    //
    protected Node(Colour z, List<Node> a, Node[] s) {
        this(z, null, a, s);
        zOrder = new Hue[z.size()];
        int counter = 0;
        for (Hue h : z) {
            zOrder[counter] = h;
        }
        this.zOrder = zOrder;
    }



    //for creating root with natural hue order
    protected Node(Colour z) {
        this(z, null, null);
    }
    protected Node(Colour z, Hue firstHue) {
        this(z, null, null);
     //   List<Node> s =
    }
    */
}
