package Prover.Tableau;

import Prover.Formula.*;

import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.SOME;
import static Prover.StatusMessage.*;
import static Prover.Tableau.ExtendResult.*;

//investigate:

//write all hue orders and change extendstructure etc
//change rx etc
//make method for temp colour
//no successors (OK I THINK)
//no solution with old hues (OK)
//two roots (OK)
//leafs in results (OK)
//X~p vs ~Xp (OK)
//TODO: hues vs xhues; changing must change all calculations

public class FHuesBreadthTableau extends Tableau {
    //TODO: write something in report or do something about some hues having both ~Xp and ~X~p (try G(p & Xp & E(X~p))
    //h4 h5
    //4x4
    //5x9 5x10 5x11
    //useless: h01678 12 13
    //useful: h23459 10 11
    //TODO: CAN NEVER HAVE TWO UPLINKS FROM ONE NODE INTO SAME NODE BECAUSE OF LEMMA 3.1

    public FHuesBreadthTableau(Formula f) {
        super(f);
    }

    public ExtendResult solve() {
        ExtendResult result = FAILURE;
        result.upLinks = new UpLinkTree<Pair<Colour, Hue>>();
        for (int i = 0; i < maxBranchLength; i++) {
            result = checkLevel(f.getFColours(), f.getAllHues(), null, null, i, result.upLinks, false);
            if (result == SUCCESS) {
                break;
            } else if (i != maxBranchLength - 1){
                result = checkLevel(f.getFColours(), f.getAllHues(), null, null, i, result.upLinks, true);
            }
        }
        return result;
    }

    private ExtendResult checkUpLinks(Node n, Hue hueToCheck, UpLinkTree<Pair<Colour, Hue>> checkedUpLinks) {
        //checked upLinks do not exist->if
        ANCESTOR_LOOP:
        for (Node a : n.ancestors) {
            if (hueToCheck.getSuccessors(f.getAllHues()).contains(a.zOrder.get(0)) &&
                    n.z.getSuccessors(f.getAllColours()).contains(a.z)) {

                addUpLink(n, a, hueToCheck);
                statusPrint(TABLEAU, MAX, "Adding uplink to " + a.getName() + " and initiating LG with");
                statusPrint(TABLEAU, MAX, infoString());
                lgRuns++;
                if (LG.check(f, root)) {
                    statusPrint(TABLEAU, MAX, "LG OK");
                    checkedUpLinks.add(new Pair(n.z, n.zOrder.get(0)), hueToCheck, n);
                    return SUCCESS;
                } else {
                    statusPrint(TABLEAU, MAX, "LG FAILED");
                    removeUpLink(n, a);
                    continue ANCESTOR_LOOP;
                    //NEEDS TO BE SOMEWHERE ELSE
                }
            }
        }
        statusPrint(TABLEAU, MAX,"All ancestors checked. No upLinks possible");
        checkedUpLinks.add(new Pair(n.z, n.zOrder.get(0)), hueToCheck, new UpLinkTree<Pair<Colour, Hue>>());
        return FAILURE;
    }

    private ExtendResult checkLevel(ColourSet possibleColours, HueSet possibleHues, Node parent, Hue parentHue, int level, UpLinkTree<Pair<Colour, Hue>> checkedUpLinks, boolean checkAll) {
        //TODO: check for empty possiblecolours
        ExtendResult result = FAILURE;
        sectionPrint(TABLEAU, SOME, "LEVEL " + level);
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

                    ColourSet possibleChildColours = c.getSuccessors(f.getAllColours());
                    if (!createDummies(n, possibleChildColours)) {
                        removeNode(n);
                        continue COLOUR_LOOP;
                    }

                    HUES_IN_NZ_LOOP:
                    for (int i = 0; i < n.z.size(); i++) {
                        Hue hueToCheck = n.zOrder.get(i);
                        statusPrint(TABLEAU, SOME, "Checking children/upLinks for " + hueToCheck.name);

                        //remove dummy leaf
                        Node dummyCopy = n.successors.get(i);
                        removeNode(dummyCopy);

                        //CHECK FOR ALREADY CHECKED UPLINKS HERE
                        UpLinkTree<Pair<Colour, Hue>> hueUpLinks = checkedUpLinks.getUpLinks(new Pair(c, firstHue), hueToCheck);
                        if (hueUpLinks != null) {
                            Node checkedChild = hueUpLinks.getNode();

                            //CASE 1: we have a previously checked uplink. We attach it to the current tree
                            if (checkedChild != null) {
                                statusPrint(TABLEAU, SOME, "Found previously checked uplink to " + checkedChild.getName());
                                restoreUpLink(n, checkedChild.getName(), hueToCheck);
                                continue HUES_IN_NZ_LOOP;

                                //CASE 2: we have an empty uplinktree, indicating a previously failed uplink check if the level
                                //is 0. We attach a leaf and continue with the next hue
                            } else if (level == 0) {
                                if (hueUpLinks.getMap() != null && hueUpLinks.getMap().isEmpty()) {
                                    statusPrint(TABLEAU, SOME, "Previously checked result: no upLinks possible");
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
                                    throw new AssertionError("Incorrectly built FHueUpLinkTree");
                                }

                                //CASE 3: we have an existing uplinktree (which is not a node), and the level is not 0.
                                //we recurse down to the lower levels
                            } else {
                                HueSet possibleChildHues = n.zOrder.get(i).getSuccessors(f.getAllHues());
                                ExtendResult lowerLevelResult = checkLevel(possibleChildColours, possibleChildHues, n, hueToCheck, level - 1, hueUpLinks, checkAll);
                                switch (lowerLevelResult) {
                                    case SUCCESS:
                                        continue HUES_IN_NZ_LOOP;
                                    case FAILURE:
                                        if (checkAll) {
                                            addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                            continue HUES_IN_NZ_LOOP;
                                        } else {
                                            removeNode(n);
                                            continue FIRST_HUE_LOOP;
                                        }
                                    default: //TODO
                                }
                            }
                            //CASE 4: there is no existing uplinktree. The level should be 0. We check the upLinks
                        } else {
                            //checked upLinks do not exist->if
                            if (level != 0) {
                                throw new AssertionError("Incorrectly built FHueUpLinkTree");
                            } else {
                                switch(checkUpLinks(n, hueToCheck, checkedUpLinks)) {
                                    case SUCCESS:
                                        continue HUES_IN_NZ_LOOP;
                                    case FAILURE:
                                        if (checkAll) {
                                            addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                            continue HUES_IN_NZ_LOOP;
                                        } else {
                                            removeNode(n);
                                            continue FIRST_HUE_LOOP;
                                        }
                                    default://TODO: error
                                }
                            }
                        }
                    }
                    if (!checkAll) {
                        result = SUCCESS;
                        break COLOUR_LOOP;
                    } else {
                        removeNode(n);
                    }
                }
            }
        }
        if (!checkAll) {
            if (result == SUCCESS) {
                statusPrint(TABLEAU, SOME, "LEVEL " + level + " SUCCESS");
            } else {
                statusPrint(TABLEAU, SOME, "LEVEL " + level + " FAILURE");
            }
        } else {
            statusPrint(TABLEAU, SOME, "LEVEL " + level + "CHECKALL FINISHED");
        }
        result.upLinks = checkedUpLinks;
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
        LG.check(f, root);
    }
    public void test() {
        Node testroot = new Node(f.getAllColours().getColour(3), f.getAllHues().getHue(2));
        Node n1 = addLeaf(testroot, f.getAllHues().getHue(2), f.getAllColours().getColour(30), f.getAllHues().getHue(8));
        Node n2 = addLeaf(n1, f.getAllHues().getHue(8), f.getAllColours().getColour(2), f.getAllHues().getHue(1));
        addUpLink(n2, testroot, f.getAllHues().getHue(1));
        root = testroot;
        LG.check(f, root);
    }

}
