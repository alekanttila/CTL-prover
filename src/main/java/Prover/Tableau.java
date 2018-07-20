package Prover;

import java.util.*;

public class Tableau {

    private enum ExtendResult {
        SUCCESS, FAILURE, STEPSCOMPLETE
    }

    //TODO: do we even need t? i don't think so
    //private TreeSet<Node> t;
    private Node root;
    //private Queue<Node> upLinksNotTried;
    private int steps = 0;
    private int maxSteps = -1;
    private final Formula f;

    //private final ColourSet colours;
    private final int maxBranchLength;

    class Node {
        private Map<Integer, Node> successors;
        //note: must order colour!
        private final Colour z;
        private final List<Hue> zOrder;
        private final List<Node> ancestors;
        private final int branchLength;

        //standard node creation
        protected Node(Colour c, Hue firstHue, Node parent) {
            //don't add to t here yet, keep external things separate
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
            if (parent != null) {
                this.ancestors = new ArrayList<Node>(parent.ancestors);
                this.ancestors.add(this);
                this.branchLength = parent.branchLength + 1;
            } else {
                this.ancestors = new ArrayList<>();
                this.ancestors.add(this);
                this.branchLength = 1;
            }
            this.successors = new HashMap<Integer, Node>();
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

    public Tableau(Formula f) {
        //TODO: check result set
        this.f = f;
        //TODO: remove duplication (since these in result set)???
        this.maxBranchLength = 1000;//TODO: replace
    }

    //init tree (CHOOSE ROOT COLOUR)
    //
    //FOR (LEFTMOST N IN UNCHECKED NODES)
    //EXTEND N:
    //  CHECK UPLINKS FOR N
    //      CHECK RX
    //          CHECK LG
    //              ADD UPLINK
    //              RETURN SUCCESS
    //      ELSE
    //          IF ANCESTOR-LENGTH OK
    //              FOR ALL HUES H IN N
    //                  FOR ALL SUCCESSOR-COLOURS C FOR H
    //                      ADD LEAF L WITH C (NEW LEFTMOST N IN UNCHECKED NODES)
    //                      IF EXTEND L RETURNS SUCCESS, RETURN SUCCESS
    //              RETURN FAIL
    //          ELSE RETURN FAIL
    //
    //CHOOSE DIFFERENT ROOT COLOUR

    public ExtendResult solve() {
        ExtendResult result = null;
        ColourSet fColours = f.getFColours();
        if (fColours.isEmpty()) {
           result = ExtendResult.FAILURE;
        }
        A:
        for (Colour c : fColours) {
            for (Hue h : c) {
                root = new Node(c, h, null);
                System.out.println("creating root with first hue " + h.name);
                switch (extend(root)) {
                    case SUCCESS:
                        result = ExtendResult.SUCCESS;
                        break A;
                    case FAILURE:
                        result = ExtendResult.FAILURE;
                        break;
                    case STEPSCOMPLETE:
                        //Should only happen when stepsolve is used
                        result = ExtendResult.STEPSCOMPLETE;
                        break A;
                }
            }
        }
        return result;
    }

    public ExtendResult stepSolve(int stepNumber) {
        maxSteps = stepNumber;
        return solve();
    }

    private ExtendResult extend(Node n) {
        ExtendResult result = null;
        if (maxSteps == -1 || steps <= maxSteps) {
            if (upLinkCheck(n)) {
                System.out.println("uplinkcheck ok");
                steps++;
                result = ExtendResult.SUCCESS;
            } else if (n.branchLength <= maxBranchLength) {
                System.out.println("adding leaves");
                ColourSet successorColours = f.getAllColours().getColourSuccessors(n.z);
                A:
                //iterating through zOrder in key order
                for (Hue h : n.zOrder) {
                    System.out.println("adding leaves for " + h.name);
                    HueSet successorHues = f.getAllHues().getHueSuccessors(h);
                    B:
                    for (Colour c : successorColours) {
                        for (Hue s : successorHues) {
                            //TODO: CHECK THIS!!
                            //want to repeat colours since the succeeding hue (first hue)
                            //will be different each time, affecting NTP?
                            if (c.contains(s)) {
                                Node newLeaf = addLeaf(n, c, s);
                                steps++;
                                switch (extend(newLeaf)) {
                                    case SUCCESS:
                                        break B;
                                    case FAILURE:
                                        //TODO: REMOVE SUCCESSORSh
                                        removeNode(newLeaf);
                                        //t.remove(newLeaf);
                                        break;
                                    case STEPSCOMPLETE:
                                        result = ExtendResult.STEPSCOMPLETE;
                                        break A;
                                }
                            }
                        }
                    }
                    result = ExtendResult.FAILURE;
                    break A;
                }
            }
        }
        return result;
    }

    public Node addLeaf(Node parent, Colour c, Hue h) {
        Node newLeaf = new Node(c, h, parent);
        parent.successors.put(parent.zOrder.indexOf(h), newLeaf);
        //t.add(newLeaf);
        return newLeaf;
    }

    public void addUpLink(Node parent, Node child, Hue h) {
        child.successors.put(child.zOrder.indexOf(h), parent);
    }

    public boolean upLinkCheck(Node node) {
        boolean result = false;
        for (Hue h : node.zOrder) {
            System.out.println("uplinkcheck for " + h.name);
            for (Node a : node.ancestors) {
                if (h.rX(a.zOrder.get(0))) {
                    //check LG
                    addUpLink(node, a, h);
                    result = true;
                }
            }
        }
        return result;
    }

    private void removeNode(Node node) {
        Node parent = node.ancestors.get(node.ancestors.size() - 1);
        parent.successors.remove(node);
    }


}
