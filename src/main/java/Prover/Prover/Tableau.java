package Prover.Prover;

import Prover.Formula.*;

import java.util.*;

import static Prover.Prover.Tableau.ExtendResult.SUCCESS;
import static Prover.Prover.Tableau.ExtendResult.FAILURE;
import static Prover.StatusMessage.finePrint;
import static Prover.StatusMessage.newSectionPrint;
import static Prover.StatusMessage.statusPrint;

public class Tableau {

    private class CheckedUpLinkTree {
        //TODO: store numbers, not colours. hues
        private Map<Pair<Pair<Colour, Hue>, Hue>, CheckedUpLinkTree> map;
        private Node n;
        private CheckedUpLinkTree getUpLinks(Colour c, Hue firstHue, Hue successorIndex) {
            return map.get(new Pair<>(new Pair<>(c, firstHue), successorIndex));
        }
        private boolean isNode() {
            boolean result = false;
            if (n!=null) {
                if (map == null) {
                    result = true;
                } else {
                    //TODO: throw assertionerro
                }
            } else {
                if (map != null) {
                    result = false;
                } else {
                    //TODO: throw assertionerro
                }
            }
            return result;
        }

        private CheckedUpLinkTree() {
            this.map = new HashMap<>();
        }
        private CheckedUpLinkTree(Node n) {
            this.n = n;
        }
        private void add(Colour c, Hue firstHue, Hue successorIndex, Node n) {
            this.map.put(new Pair<>(new Pair<>(c, firstHue), successorIndex), new CheckedUpLinkTree(n));
        }
        private void add(Colour c, Hue firstHue, Hue successorIndex, CheckedUpLinkTree tree) {
            this.map.put(new Pair<>(new Pair<>(c, firstHue), successorIndex), tree);
        }
    }

    protected enum ExtendResult {
        SUCCESS, FAILURE, STEPSCOMPLETE;
        private CheckedUpLinkTree checkedUpLinks;
    }

    //TODO: do we even need t? i don't think so
    //private TreeSet<Node> t;
    private Node root;
    //private Queue<Node> upLinksNotTried;
    private int steps = 0;
    private int operations = 0;
    private int maxSteps = -1;
    private final Formula f;

    //private final ColourSet colours;
    private final int maxBranchLength;


    public void printInfo() {
        printInfo(root);
    }

    public void printInfo(Node n) {
        Set<Node> nodes = new HashSet<>();
        nodes.add(n);
        System.out.println("PRINTING NODE INFO");
        n.z.namePrint();
        for (int i = 0; i < n.successors.size(); i++) {
            Node s = n.successors.get(i);
            if (nodes.contains(s)) {
                continue;
            }
            printInfo(s);
        }
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
    //              FOR ALL SUCCESSOR-COLOURS C OF N
    //                  FOR ALL SUCCESSOR-HUES OF N H IN C
    //                      ADD LEAF L WITH C AND FIRST HUE H (NEW LEFTMOST N IN UNCHECKED NODES)
    //                      IF EXTEND L RETURNS SUCCESS, RETURN SUCCESS
    //              RETURN FAIL
    //          ELSE RETURN FAIL
    //
    //CHOOSE DIFFERENT ROOT COLOUR


    //for hue H in z(N)
    //  for C in successor colours of H
    //      for hue H2 in successor hues of H in C

    //for hue H in z(N)

    //for colour C in possible colours
    //  for all possible first hues in C
    //      tryAllUplinks
    //      SUCCESS-> return SUCCESS

    //TRYING TO GET ALL UPLINKS FOR SPECIFIC COLOUR AND SPECIFIC ORDER:
    //  add leaf successors for all hues H2 in Z(N) that come after H
    //      for all ancestors A of N
    //          check H2 RX zA(0)
    //              yes->check LG
    //                  yes->create uplink and steps++ and continue (remove one leaf and check)
    //      no uplink for all A-> return FAIL
    //

    //TODO: justify not searching ALL tabluau

    //FOR (LEFTMOST N IN UNCHECKED NODES)
    //EXTEND N:
    //  CHECK UPLINKS FOR N
    //      FOR EACH HUE H IN N
    //          CHECK
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

    public ExtendResult solveBreadthFirst() {
        ExtendResult result = FAILURE;
        result.checkedUpLinks = new CheckedUpLinkTree();
        result = checkLevelUpLinks(f.getFColours(), f.getAllHues(), null, null, 0, result.checkedUpLinks, false);
        result = checkLevelUpLinks(f.getFColours(), f.getAllHues(), null, null, 0, result.checkedUpLinks, true);
        /*
        for (int i = 0; i < maxBranchLength; i++) {
            result = checkLevelUpLinks(f.getFColours(), f.getAllHues(), null, null, i, result.checkedUpLinks, false);
            if (result == SUCCESS) {
                break;
            } else {
                result = checkLevelUpLinks(f.getFColours(), f.getAllHues(), null, null, i, result.checkedUpLinks, true);
            }
        }*/
        return result;
    }

    private ExtendResult checkLevelUpLinks(ColourSet possibleColours, HueSet possibleHues, Node parent, Hue parentHue, int level, CheckedUpLinkTree checkedUpLinks, boolean checkAll) {
        //TODO: check for empty possiblecolours
        ExtendResult result = null;
        if (level == 0) {
            newSectionPrint("LEVEL 0");
            statusPrint("Possible colours");
            statusPrint(possibleColours.hueString());
            statusPrint("Possible hues");
            statusPrint(possibleHues.nameString());
            COLOUR_LOOP:
            for (Colour c : possibleColours) {
                statusPrint("Fixing colour " + c.name);
                HUE_LOOP:
                for (Hue firstHue : possibleHues) {
                    finePrint("Checking first hue " + firstHue.name);
                    if (c.contains(firstHue)) {
                        statusPrint("Fixing first hue " + firstHue.name);
                        Node n = addLeaf(parent, parentHue, c, firstHue);
                        if (root == null) {
                            root = n;
                            statusPrint("(This is the new root)");
                        }
                        ColourSet possibleChildColours = f.getAllColours().getColourSuccessors(c);
                        finePrint("Possible child colours:");
                        finePrint(possibleChildColours.nameString());
                        DUMMY_CREATION_LOOP:
                        for (Hue predecessorHue : n.zOrder) {
                            HueSet possibleSuccessorHues = f.getAllHues().getHueSuccessors(predecessorHue);
                            //MAKE DUMMY CHILDREN
                            for (Colour candidateColour : possibleChildColours) {
                                for (Hue candidateHue : possibleSuccessorHues) {
                                    if (candidateColour.contains(candidateHue)) {
                                        finePrint("Creating dummy leaf for hue " + predecessorHue.name + " with " + candidateColour.name + " and " + candidateHue.name);
                                        addLeaf(n, predecessorHue, candidateColour, candidateHue);
                                        continue DUMMY_CREATION_LOOP;
                                    }
                                }
                            }
                            //can't make a successor node for a hue->failure
                            //don't want to check even if checkall is true
                            statusPrint("Couldn't make dummies; choosing new colour");
                            continue COLOUR_LOOP;
                        }

                        HUES_IN_NZ_LOOP:
                        for (int i = 0; i < n.z.size(); i++) {
                            Hue hueToCheck = n.zOrder.get(i);
                            statusPrint("Checking uplinks for " + hueToCheck.name);
                            //remove dummy leaf
                            Node dummyCopy = n.successors.get(i);
                            removeNode(dummyCopy);

                            //CHECK FOR ALREADY CHECKED UPLINKS HERE
                            CheckedUpLinkTree hueUpLinks = checkedUpLinks.getUpLinks(c, firstHue, hueToCheck);
                            if (hueUpLinks != null) {
                                Node checkedChild = hueUpLinks.n;
                                if (checkedChild != null) {
                                    statusPrint("Found previously checked uplink to " + checkedChild.getName());
                                    addUpLink(n, checkedChild, hueToCheck);
                                    continue HUES_IN_NZ_LOOP;
                                } else if (hueUpLinks.map != null && hueUpLinks.map.isEmpty()) {
                                    statusPrint("Previously checked result: no uplinks possible");
                                    if (checkAll) {
                                        addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                        continue HUES_IN_NZ_LOOP;
                                    } else {
                                        continue HUE_LOOP;
                                    }
                                } else {
                                    throw new AssertionError("Incorrectly built CheckedUpLinkTree");
                                }
                            } else {
                                ANCESTOR_LOOP:
                                for (Node a : n.ancestors) {
                                    if (firstHue.rX(a.zOrder.get(0))) {
                                        addUpLink(n, a, firstHue);
                                        statusPrint("Adding uplink to " + a.getName() + " and initiating LG");
                                        if (LG3.check(f, root)) {
                                            checkedUpLinks.add(c, firstHue, hueToCheck, n);
                                            continue HUES_IN_NZ_LOOP;
                                        } else {
                                            removeUpLink(n, a);
                                            continue ANCESTOR_LOOP;
                                            //NEEDS TO BE SOMEWHERE ELSE
                                        }
                                    }
                                }
                                statusPrint("All ancestors checked. No uplinks possible");
                                checkedUpLinks.add(c, firstHue, hueToCheck, new CheckedUpLinkTree());
                                removeNode(n);
                                if (checkAll) {
                                    addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                    continue HUES_IN_NZ_LOOP;
                                } else {
                                    continue HUE_LOOP;
                                }
                            }
                        }
                        result = SUCCESS;
                        break COLOUR_LOOP;
                    }
                }
            }
            result = FAILURE;
        } else {
            COLOUR_LOOP:
            for (Colour c: possibleColours) {
                POSSIBLE_HUE_ORDER_LOOP:
                for (Hue firstHue: possibleHues) {
                    if (c.contains(firstHue)) {
                        Node n = addLeaf(parent, parentHue, c, firstHue);
                        if (root == null) {
                            root = n;
                        }
                        ColourSet possibleChildColours = f.getAllColours().getColourSuccessors(c);
                        DUMMY_CREATION_LOOP:
                        for (Hue predecessorHue : n.zOrder) {
                            HueSet possibleSuccessorHues = f.getAllHues().getHueSuccessors(predecessorHue);
                            //MAKE DUMMY CHILDREN
                            for (Colour candidateColour : possibleChildColours) {
                                for (Hue candidateHue : possibleSuccessorHues) {
                                    if (candidateColour.contains(candidateHue)) {
                                        addLeaf(n, predecessorHue, candidateColour, candidateHue);
                                        continue DUMMY_CREATION_LOOP;
                                    }
                                }
                            }
                            //can't make a successor node for a hue->failure
                            //don't want to check even if checkall is true
                            continue COLOUR_LOOP;
                        }
                        //NOTE: using n.z.size instead of successors.size here; think
                        HUES_IN_NZ_LOOP:
                        for (int i = 0; i < n.z.size(); i++) {
                            Hue hueToCheck = n.zOrder.get(i);
                            //remove dummy leaf
                            Node dummyCopy = n.successors.get(i);
                            removeNode(dummyCopy);

                            //CHECK FOR ALREADY CHECKED UPLINKS HERE
                            CheckedUpLinkTree hueUpLinks = checkedUpLinks.getUpLinks(c, firstHue, hueToCheck);
                            if (hueUpLinks == null) {
                                throw new AssertionError("Incorrectly built CheckedUpLinkTree");
                            }
                            Node checkedChild = hueUpLinks.n;
                            if (checkedChild != null) {
                                addUpLink(n, checkedChild, hueToCheck);
                                continue HUES_IN_NZ_LOOP;
                            } else {
                                HueSet possibleChildHues = f.getAllHues().getHueSuccessors(n.zOrder.get(i));
                                switch (checkLevelUpLinks(possibleChildColours, possibleChildHues, n, hueToCheck, level - 1, hueUpLinks, checkAll)) {
                                    case SUCCESS:
                                        continue HUES_IN_NZ_LOOP;
                                    case FAILURE:
                                        if (checkAll) {
                                            //TODO: change?
                                            addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                            continue  HUES_IN_NZ_LOOP;
                                        } else {
                                            removeNode(n);
                                            continue POSSIBLE_HUE_ORDER_LOOP;
                                        }
                                }
                            }
                            if (!checkAll) {
                                result = SUCCESS;
                                break COLOUR_LOOP;
                            }
                        }
                    }
                }
            }
        }
        result.checkedUpLinks = checkedUpLinks;
        return result;
    }

    public ExtendResult solve() {
        ExtendResult result = null;
        ColourSet fColours = f.getFColours();
        if (fColours.isEmpty()) {
           result = ExtendResult.FAILURE;
        }
        A:
        for (Colour c : fColours) {
            for (Hue h : c) {
                root = new Node(c, h);
                System.out.println("creating root with first hue " + h.name);
                switch (extend(root)) {
                    case SUCCESS:
                        result = SUCCESS;
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

    //TODO: think: can it be the case that given a certain hue order for a node, we can uplink a hue, and find no uplinks for the rest of the hues, but with a different order, all the hues would eventually uplink?

    private ExtendResult extend(Node n) {
        ExtendResult result = null;
        if (maxSteps == -1 || steps <= maxSteps) {
            if (upLinkCheck(n)) {
                System.out.println("uplinkcheck ok");
                steps++;
                result = SUCCESS;
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
                                /*
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
                                */
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

    public Node addLeaf(Node parent, Hue predecessorHue, Colour c, Hue firstHue) {
        Node newLeaf;
        if (parent != null) {
            int index = parent.zOrder.indexOf(predecessorHue);
            newLeaf = new Node(c, firstHue, parent, "" + parent.zOrder.indexOf(predecessorHue));
            parent.successors.put(index, newLeaf);
        } else {
            newLeaf = new Node(c, firstHue);
        }
        return newLeaf;
    }

    public void addUpLink(Node parent, Node child, Hue h) {
        parent.successors.put(parent.zOrder.indexOf(h), child);
    }

    public void removeUpLink(Node parent, Node child) {
        parent.successors.remove(child);
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
        if (node != root) {
            List<Node> test = node.ancestors;
            System.out.println(node.ancestors.size());
            Node parent = node.ancestors.get(node.ancestors.size() - 2);
            parent.successors.remove(node);
        } else {
            root = null;
        }
    }


}
