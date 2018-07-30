package Prover.Prover;

import Prover.Formula.*;
import Prover.StatusMessage;
import Prover.TextMenu;

import java.util.*;

import static Prover.Prover.Tableau.ExtendResult.*;
import static Prover.StatusMessage.*;
import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.NONE;
import static Prover.StatusMessage.Level.SOME;

//investigate:

//write all hue orders and change extendstructure etc
//change rx etc
//make method for temp colour
//no successors (OK I THINK)
//no solution with old hues (OK)
//two roots (OK)
//leafs in results (OK)
//X~p vs ~Xp (OK)

public class Tableau {
    //TODO: write something in report or do something about some hues having both ~Xp and ~X~p (try G(p & Xp & E(X~p))
    //h4 h5
    //4x4
    //5x9 5x10 5x11
    //useless: h01678 12 13
    //useful: h23459 10 11
    //TODO: CAN NEVER HAVE TWO UPLINKS FROM ONE NODE INTO SAME NODE BECAUSE OF LEMMA 3.1

    private class CheckedUpLinkTree {
        //TODO: store numbers, not colours. hues
        private Map<Pair<Pair<Colour, Hue>, Hue>, CheckedUpLinkTree> map;
        private Node n;
        private CheckedUpLinkTree getUpLinks(Colour c, Hue firstHue, Hue successorIndex) {
            return map.get(new Pair<>(new Pair<>(c, firstHue), successorIndex));
        }

        @Override
        public boolean equals(Object obj) {
            boolean result;
            if (this == obj) {
                result = true;
            } else if (obj.getClass() != CheckedUpLinkTree.class) {
                result = false;
            } else {
                CheckedUpLinkTree c = (CheckedUpLinkTree)obj;
                if (this.isNode()) {
                    if (c.isNode()) {
                        //TODO: note somewhere we only use copies of nodes so that this works?
                        result = (this.n == c.n);
                    } else {
                        result = false;
                    }
                } else {
                    if (c.isNode()) {
                        result = false;
                    } else {
                        result = this.map.equals(c.map);
                    }
                }
            }
            return result;
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
            if (this.n == null) {
                this.map.put(new Pair<>(new Pair<>(c, firstHue), successorIndex), new CheckedUpLinkTree(n));
            } else {
                throw new AssertionError();
                //TODO: change the error here
            }
        }
        private void add(Colour c, Hue firstHue, Hue successorIndex, CheckedUpLinkTree tree) {
            if (this.n == null) {
                this.map.put(new Pair<>(new Pair<>(c, firstHue), successorIndex), tree);
            } else {
                throw new AssertionError();
                //TODO:change the error here
            }
        }
    }

    protected enum ExtendResult {
        SUCCESS, FAILURE, STEPS_COMPLETE, CHECK_NEXT_HUE, START_NEW_FIRST_HUE;
        private CheckedUpLinkTree checkedUpLinks;
    }

    //TODO: do we even need t? i don't think so
    //private TreeSet<Node> t;
    private Node root;
    //private Queue<Node> upLinksNotTried;
    private int steps = 0;
    private int lgRuns = 0;
    private int maxSteps = -1;
    private final Formula f;

    //private final ColourSet colours;
    private final int maxBranchLength;


    public String infoString() {
        return infoString(root, null);
    }

    public String infoString(Node n, Set<Node> printedNodes) {
        String result = "";
        if (printedNodes == null) {
            printedNodes = new HashSet<>();
        }
        printedNodes.add(n);
        result = result + n.getName() + " " + n.z.name + " {";
        for (int i = 0; i < n.zOrder.size(); i++) {
            Hue h = n.zOrder.get(i);
            result = result + h.name;
            if (i != n.zOrder.size() -1) {
                result = result + ", ";
            }
        }
        result = result + "}";
        result = result + " Successors:";
        result = result + n.successors.size() + " ";
        Iterator<Node> i = n.successors.values().iterator();
        while (i.hasNext()) {
            Node s = i.next();
            result = result + s.getName() + " ";
        }
        result = result + "\n";
        Iterator<Node> i2 = n.successors.values().iterator();
        while (i2.hasNext()) {
            Node s = i2.next();
            if (printedNodes.contains(s)) {
                continue;
            }
            result = result + infoString(s, printedNodes);
        }
        return result;
    }

    public Tableau(Formula f) {
        //TODO: check result set
        this.f = f;
        //TODO: remove duplication (since these in result set)???
        this.maxBranchLength = 2;//TODO: replace
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
        for (int i = 0; i < maxBranchLength; i++) {
            result = checkLevelUpLinks(f.getFColours(), f.getAllHues(), null, null, i, result.checkedUpLinks, false);
            if (result == SUCCESS) {
                break;
            } else if (i != maxBranchLength - 1){
                result = checkLevelUpLinks(f.getFColours(), f.getAllHues(), null, null, i, result.checkedUpLinks, true);
            }
        }
        return result;
    }

    private boolean createDummies(Node n, ColourSet possibleChildColours) {
        boolean result = true;
        DUMMY_CREATION_LOOP:
        for (Hue predecessorHue : n.zOrder) {
            HueSet possibleSuccessorHues = f.getAllHues().getHueSuccessors(predecessorHue);
            //MAKE DUMMY CHILDREN
            for (Colour candidateColour : possibleChildColours) {
                for (Hue candidateHue : possibleSuccessorHues) {
                    if (candidateColour.contains(candidateHue)) {
                        statusPrint(TABLEAU, MAX, ("Creating dummy leaf for hue " + predecessorHue.name + " with " + candidateColour.name + " and " + candidateHue.name));
                        addLeaf(n, predecessorHue, candidateColour, candidateHue);
                        continue DUMMY_CREATION_LOOP;
                    }
                }
            }
            //can't make a successor node for a hue->failure
            //don't want to check even if checkall is true
            statusPrint(TABLEAU, SOME, "Couldn't make dummies; choosing new colour");
            result = false;
        }
        return  result;
    }

    private ExtendResult level0UpLinkCheck(Node n, Colour c, Hue firstHue, Hue hueToCheck, Node dummyCopy, CheckedUpLinkTree checkedUpLinks, boolean checkAll) {
        //checked uplinks do not exist->if
        ANCESTOR_LOOP:
        for (Node a : n.ancestors) {
            if (hueToCheck.rX(a.zOrder.get(0))) {
                //can skip double uplinks because of lemma 3.1
                //TODO: write in report
                //TODO: write in report that cases where two hues uplink to same node, but linking first causes infinite loop are covered by
                //switching hue or
                for (Hue hueInC : c) {
                    CheckedUpLinkTree potentialUpLink = checkedUpLinks.getUpLinks(c, firstHue, hueInC);
                    if (potentialUpLink != null && potentialUpLink.equals(new CheckedUpLinkTree(a))) {
                        continue ANCESTOR_LOOP;
                    }
                }

                addUpLink(n, a, hueToCheck);
                statusPrint(TABLEAU, MAX, "Adding uplink to " + a.getName() + " and initiating LG with");
                statusPrint(TABLEAU, MAX, infoString());
                lgRuns++;
                if (LG3.check(f, root)) {
                    statusPrint(TABLEAU, MAX, "LG OK");
                    checkedUpLinks.add(c, firstHue, hueToCheck, n);
                    return CHECK_NEXT_HUE;
                } else {
                    statusPrint(TABLEAU, MAX, "LG FAILED");
                    removeUpLink(n, a);
                    continue ANCESTOR_LOOP;
                    //NEEDS TO BE SOMEWHERE ELSE
                }
            }
        }
        statusPrint(TABLEAU, MAX,"All ancestors checked. No uplinks possible");
        checkedUpLinks.add(c, firstHue, hueToCheck, new CheckedUpLinkTree());
        if (checkAll) {
            addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
            return CHECK_NEXT_HUE;
        } else {
            removeNode(n);
            return START_NEW_FIRST_HUE;
        }
    }

    private ExtendResult checkLevelUpLinks(ColourSet possibleColours, HueSet possibleHues, Node parent, Hue parentHue, int level, CheckedUpLinkTree checkedUpLinks, boolean checkAll) {
        //TODO: check for empty possiblecolours
        ExtendResult result = FAILURE;
        sectionPrint(TABLEAU, SOME, "LEVEL " + level);
        statusPrint(TABLEAU, MAX, "Possible colours");
        statusPrint(TABLEAU, MAX, possibleColours.hueString());
        statusPrint(TABLEAU, MAX, "Possible first hues");
        statusPrint(TABLEAU, MAX, possibleHues.nameString());
        COLOUR_LOOP:
        for (Colour c : possibleColours) {
            subSectionPrint(TABLEAU, SOME, "Fixing colour " + c.name);

            FIRST_HUE_LOOP:
            for (Hue firstHue : possibleHues) {
                statusPrint(TABLEAU, MAX, "Checking first hue " + firstHue.name);

                if (c.contains(firstHue)) {
                    statusPrint(TABLEAU, SOME, "Fixing first hue " + firstHue.name);

                    Node n = addLeaf(parent, parentHue, c, firstHue);
                    if (root == null) {
                        root = n;
                        statusPrint(TABLEAU, SOME, "(This is the new root)");
                    }

                    ColourSet possibleChildColours = f.getAllColours().getColourSuccessors(c);
                    statusPrint(TABLEAU, MAX, "Possible child colours:");
                    statusPrint(TABLEAU, MAX, possibleChildColours.nameString());
                    if (!createDummies(n, possibleChildColours)) {
                        removeNode(n);
                        continue COLOUR_LOOP;
                    }

                    HUES_IN_NZ_LOOP:
                    for (int i = 0; i < n.z.size(); i++) {
                        Hue hueToCheck = n.zOrder.get(i);
                        statusPrint(TABLEAU, SOME, "Checking children/uplinks for " + hueToCheck.name);

                        //remove dummy leaf
                        Node dummyCopy = n.successors.get(i);
                        removeNode(dummyCopy);

                        //CHECK FOR ALREADY CHECKED UPLINKS HERE
                        CheckedUpLinkTree hueUpLinks = checkedUpLinks.getUpLinks(c, firstHue, hueToCheck);
                        if (hueUpLinks != null) {
                            Node checkedChild = hueUpLinks.n;

                            //CASE 1: we have a previously checked uplink. We attach it to the current tree
                            if (checkedChild != null) {
                                statusPrint(TABLEAU, SOME, "Found previously checked uplink to " + checkedChild.getName());
                                restoreUpLink(n, checkedChild.getName(), hueToCheck);
                                continue HUES_IN_NZ_LOOP;

                                //CASE 2: we have an empty uplinktree, indicating a previously failed uplink check if the level
                                //is 0. We attach a leaf and continue with the next hue
                            } else if (level == 0) {
                                if (hueUpLinks.map != null && hueUpLinks.map.isEmpty()) {
                                    statusPrint(TABLEAU, SOME, "Previously checked result: no uplinks possible");
                                    if (checkAll) {
                                        addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                        continue HUES_IN_NZ_LOOP;
                                        //should not be able to reach this unless there has been a previous check;
                                        //the method is meant to be used with checkAll off at each level first
                                    } else {
                                        throw new AssertionError("Incorrect level check sequence");
                                    }
                                    //if the uplinktree exists and has no node, and the level is 0, the uplinktree map
                                    //should be nonnull and empty
                                } else {
                                    throw new AssertionError("Incorrectly built CheckedUpLinkTree");
                                }

                                //CASE 3: we have an existing uplinktree (which is not a node), and the level is not 0.
                                //we recurse down to the lower levels
                            } else {
                                HueSet possibleChildHues = f.getAllHues().getHueSuccessors(n.zOrder.get(i));
                                ExtendResult lowerLevelResult = checkLevelUpLinks(possibleChildColours, possibleChildHues, n, hueToCheck, level - 1, hueUpLinks, checkAll);
                                if (!checkAll) {
                                    switch (lowerLevelResult) {
                                        case SUCCESS:
                                            continue HUES_IN_NZ_LOOP;
                                        case FAILURE:
                                            removeNode(n);
                                            continue FIRST_HUE_LOOP;
                                        default:
                                    }
                                } else {
                                    //TODO: change?
                                    addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                    continue HUES_IN_NZ_LOOP;
                                }
                            }

                            //CASE 4: there is no existing uplinktree. The level should be 0. We check the uplinks
                        } else {
                            //checked uplinks do not exist->if
                            if (level != 0) {
                                throw new AssertionError("Incorrectly built CheckedUpLinkTree");
                            } else {
                                switch(level0UpLinkCheck(n, c, firstHue, hueToCheck, dummyCopy, checkedUpLinks, checkAll)) {
                                    case CHECK_NEXT_HUE: //success or checkAll
                                        continue HUES_IN_NZ_LOOP;
                                    case START_NEW_FIRST_HUE: //failure and not checkAll
                                        continue FIRST_HUE_LOOP;
                                    default://TODO: error
                                }
                            }
                        }
                    }
                    if (checkAll) {
                        removeNode(n);
                    } else {
                        statusPrint(TABLEAU, SOME, "LEVEL " + level + " SUCCESS");
                        result = SUCCESS;
                        break COLOUR_LOOP;
                    }
                }
            }
        }
        if (!checkAll) {
            if (result != SUCCESS) {
                statusPrint(TABLEAU, SOME, "LEVEL " + level + " FAILURE");
            }
        } else {
            statusPrint(TABLEAU, SOME, "LEVEL " + level + "CHECKALL FINISHED");
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
                switch (extend(root)) {
                    case SUCCESS:
                        result = SUCCESS;
                        break A;
                    case FAILURE:
                        result = ExtendResult.FAILURE;
                        break;
                    case STEPS_COMPLETE:
                        //Should only happen when stepsolve is used
                        result = ExtendResult.STEPS_COMPLETE;
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
                steps++;
                result = SUCCESS;
            } else if (n.branchLength <= maxBranchLength) {
                ColourSet successorColours = f.getAllColours().getColourSuccessors(n.z);
                A:
                //iterating through zOrder in key order
                for (Hue h : n.zOrder) {
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

    //4,3,5 c50
    //2 c48
    //9 c67
    public void test2() {
        Node testroot = new Node(f.getAllColours().getColour(50), f.getAllHues().getHue(4));
        Node n1 = addLeaf(testroot, f.getAllHues().getHue(3), f.getAllColours().getColour(48), f.getAllHues().getHue(2));
        Node n2 = addLeaf(testroot, f.getAllHues().getHue(5), f.getAllColours().getColour(67), f.getAllHues().getHue(9));
        addUpLink(testroot, testroot, f.getAllHues().getHue(4));
        addUpLink(n1, testroot, f.getAllHues().getHue(2));
        addUpLink(n2, testroot, f.getAllHues().getHue(9));
        root = testroot;
        LG3.check(f, root);
    }
    public void test() {
        Node testroot = new Node(f.getAllColours().getColour(3), f.getAllHues().getHue(2));
        Node n1 = addLeaf(testroot, f.getAllHues().getHue(2), f.getAllColours().getColour(30), f.getAllHues().getHue(8));
        Node n2 = addLeaf(n1, f.getAllHues().getHue(8), f.getAllColours().getColour(2), f.getAllHues().getHue(1));
        addUpLink(n2, testroot, f.getAllHues().getHue(1));
        root = testroot;
        LG3.check(f, root);
    }

    public Node addLeaf(Node parent, Hue predecessorHue, Colour c, Hue firstHue) {
        //TODO: check if huerx holds, throw error if doesn't
        Node newLeaf;
        if (parent != null) {
            int index = parent.zOrder.indexOf(predecessorHue);
            newLeaf = new Node(c, firstHue, parent, "" + index);
            parent.successors.put(index, newLeaf);
        } else {
            newLeaf = new Node(c, firstHue);
        }
        return newLeaf;
    }

    public void addUpLink(Node parent, Node child, Hue h) {
        parent.successors.put(parent.zOrder.indexOf(h), child);
    }

    public Node retrieveNodeByName(String name) {
        name = name.substring(1);
        Node result = root;
        for (int i = 0; i < name.length(); i++) {
            result = result.successors.get(Integer.parseInt(Character.toString(name.charAt(i))));
        }
        return result;
    }

    public void restoreUpLink(Node parent, String childName, Hue h) {
        Node child = retrieveNodeByName(childName);
        addUpLink(parent, child, h);
    }

    public void removeUpLink(Node parent, Node child) {
        parent.successors.remove(child);
    }

    public boolean upLinkCheck(Node node) {
        boolean result = false;
        for (Hue h : node.zOrder) {
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
            Node parent = node.ancestors.get(node.ancestors.size() - 2);
            parent.successors.values().remove(node);
        } else {
            root = null;
        }
    }


}
