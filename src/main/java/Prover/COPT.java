package Prover;

import java.util.*;

public class COPT {
    private Stack<COPT> history;
    private TreeSet<Node> t;
    private Node root;
    private Queue<Node> upLinksNotTried;
    //private final ColourSet colours;
    //private final Formula f;

    private class Node {
        private Node[] successors;
        //note: must order colour!
        private Colour z;
        private Hue[] zOrder;
        private List<Node> ancestors;
        //add no. of ancestors??

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
    }

    //init tree (CHOOSE ROOT COLOUR)
    //
    //EXTEND:
    //FOR (LEFTMOST N IN UNCHECKED NODES)
    //  CHECK UPLINKS FOR N
    //      CHECK RX
    //          CHECK LG
    //              ADD UPLINK
    //      ELSE
    //          ADD LEAF (NEW LEFTMOST N IN UNCHECKED NODES)
    //          CHOOSE LEAF COLOUR; PUSH HISTORY
    //          EXTEND (IF ANCESTOR LENGTH STILL OK)
    //          ELSE BACKTRACK; CHOOSE DIFFERENT COLOUR
    //
    //CHOOSE DIFFERENT ROOT COLOUR

    /*
   public COPT(Formula f, ColourSet cS) {
        this.f = f;
        this.colours = cS;
   }*/
/*
    public COPT(COPT copt, Colour c) {
        this.f = copt.f;
        this.colours = copt.colours;
        this.root = new Node(c);
    }

   /*
    public COPT(Colour c) {
        Node root = new Node(c);
        history = new Stack<COPT>();
        history.push(this);
    }*/
/*
    public void solve() {
        ColourSet fColours = colours.getColoursWithF(f)
        for (Colour c : fColours) {
            COPT cCOPT = new COPT(this, c);
           // if (cCOPT.)
        }
    }*/

    public COPT(COPT previous) {
        this.t = previous.t;
        this.root = previous.root;
        this.upLinksNotTried = previous.upLinksNotTried;
        this.history = previous.history;
        history.push(previous);
    }

    public void addLeaf(Node parent, Colour c, Hue h) {
        List<Hue> zT = new ArrayList<Hue>();
        zT.add(h);
        c.remove(h);
        zT.addAll(c);
        List<Node> a = new ArrayList<Node>();
        a.addAll(parent.ancestors);
       // Node leaf = new Node(new ArrayList<Node>(), c, zT, a);
       // t.add(leaf);
    }

    public void addUpLink(Node parent, Node child, Hue h) {
        child.successors[h.getIndex()] = parent;
    }

    public void tryUpLinks() {
        for (Node n : this.upLinksNotTried) {
            upLinkCheck(n);
      //      n.successors.
        }
    }

    public Node upLinkCheck(Node node) {
        for (Hue h : node.zOrder) {
            for (Node a : node.ancestors) {
                if (h.rX(a.zOrder[0])) {
                    //check LG
                    addUpLink(node, a, h);
                }
            }
         //   addLeaf(node, )
        }
        return null;
    }


}
