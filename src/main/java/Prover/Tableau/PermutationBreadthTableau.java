package Prover.Tableau;

import Prover.Formula.*;

import java.util.*;

import static Prover.Tableau.ExtendResult.*;
import static Prover.StatusMessage.*;
import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.SOME;

//investigate:

//write all hue orders and change extendstructure etc
//make method for temp colour
//no successors (OK I THINK)
//no solution with old hues (OK)
//two roots (OK)
//leafs in results (OK)
//X~p vs ~Xp (OK)
//TODO: hues vs xhues; changing must change all calculations
//TODO: order colours size first?

public class PermutationBreadthTableau extends Tableau {
    //TODO: write something in report or do something about some hues having both ~Xp and ~X~p (try G(p & Xp & E(X~p))
    //h4 h5
    //4x4
    //5x9 5x10 5x11
    //useless: h01678 12 13
    //useful: h23459 10 11
    //TODO: CAN NEVER HAVE TWO UPLINKS FROM ONE NODE INTO SAME NODE BECAUSE OF LEMMA 3.1

    public PermutationBreadthTableau(Formula f) {
        super(f);
    }

    public ExtendResult solve() {
        ExtendResult result = FAILURE;
        result.upLinks = new UpLinkTree<List<Hue>>();
        for (int i = 0; i < maxBranchLength; i++) {
            result = checkAllColours( null, null, i, result.upLinks, false);
            if (result == SUCCESS) {
                break;
            } else if (i != maxBranchLength - 1){
                result = checkAllColours(null, null, i, result.upLinks, true);
            }
        }
        return result;
    }

    private ExtendResult checkUpLinks(Node n, Hue hueToCheck, UpLinkTree<List<Hue>> checkedUpLinks) {
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
                    checkedUpLinks.add(n.zOrder, hueToCheck, n);
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
        checkedUpLinks.add(n.zOrder, hueToCheck, new UpLinkTree<List<Hue>>());
        return FAILURE;
    }

    private ExtendResult checkNode(Node n, int level, UpLinkTree<List<Hue>> checkedUpLinks, boolean checkAll) {
        ExtendResult result = SUCCESS;
        ColourSet possibleChildColours = n.z.getSuccessors(f.getAllColours());
        if (!createDummies(n, possibleChildColours)) {
            result = FAILURE;
            return result;
        }

        HUES_IN_NZ_LOOP:
        for (int i = 0; i < n.z.size(); i++) {
            Hue hueToCheck = n.zOrder.get(i);
            statusPrint(TABLEAU, SOME, "Checking children/upLinks for " + hueToCheck.name);

            //remove dummy leaf
            Node dummyCopy = n.successors.get(i);
            removeNode(dummyCopy);

            //CHECK FOR ALREADY CHECKED UPLINKS HERE
            UpLinkTree<List<Hue>> hueUpLinks = checkedUpLinks.getUpLinks(n.zOrder, hueToCheck);
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
                            result = FAILURE;
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
                        throw new AssertionError("Incorrectly built PermutationUpLinkTree");
                    }

                    //CASE 3: we have an existing uplinktree (which is not a node), and the level is not 0.
                    //we recurse down to the lower levels
                } else {
                    ExtendResult lowerLevelResult = checkAllColours( n, hueToCheck, level - 1, hueUpLinks, checkAll);
                    switch (lowerLevelResult) {
                        case SUCCESS:
                            continue HUES_IN_NZ_LOOP;
                        case FAILURE:
                            result = FAILURE;
                            if (checkAll) {
                                addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                continue HUES_IN_NZ_LOOP;
                            } else {
                                break HUES_IN_NZ_LOOP;
                            }
                        default: //TODO
                    }
                }
                //CASE 4: there is no existing uplinktree. The level should be 0. We check the upLinks
            } else {
                //checked upLinks do not exist->if
                if (level != 0) {
                    throw new AssertionError("Incorrectly built PermutationUpLinkTree");
                } else {
                    switch(checkUpLinks(n, hueToCheck, checkedUpLinks)) {
                        case SUCCESS:
                            continue HUES_IN_NZ_LOOP;
                        case FAILURE:
                            result = FAILURE;
                            if (checkAll) {
                                addLeaf(n, hueToCheck, dummyCopy.z, dummyCopy.zOrder.get(0));
                                continue HUES_IN_NZ_LOOP;
                            } else {
                                break HUES_IN_NZ_LOOP;
                            }
                        default://TODO: error
                    }
                }
            }
        }
        return result;
    }

    private ExtendResult checkAllColours(Node parent, Hue parentHue, int level, UpLinkTree<List<Hue>> checkedUpLinks, boolean checkAll) {
        //TODO: check for empty possiblecolours
        ExtendResult result = FAILURE;
        sectionPrint(TABLEAU, SOME, "LEVEL " + level);

        ColourSet possibleColours;
        if (parent == null) {
            possibleColours = f.getFColours();
        } else {
            possibleColours = parent.z.getSuccessors(f.getAllColours());
        }
        COLOUR_LOOP:
        for (Colour c : possibleColours) {
            subSectionPrint(TABLEAU, SOME, "Fixing colour " + c.name);

            Map<Integer, Hue> hueOrder = new HashMap<Integer, Hue>();
            switch (checkHuePermutations(parent, parentHue, level, checkedUpLinks, checkAll, c, 0, hueOrder)) {
                case SUCCESS:
                    result = SUCCESS;
                    break COLOUR_LOOP;
                case FAILURE:
                    continue COLOUR_LOOP;
                    default:
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
        return result;
    }

    private ExtendResult checkHuePermutations(Node parent, Hue parentHue, int level, UpLinkTree<List<Hue>> checkedUpLinks, boolean checkAll, Colour c, int permutationIndex, Map<Integer, Hue> hueOrder) {
        ExtendResult result = FAILURE;
        Hue h = null;
        Iterator<Hue> hueIterator = c.iterator();
        for (int i = 0; i <= permutationIndex; i++) {
            h = hueIterator.next();
        }
        PERMUTATION_LOOP:
        for (int i = 0; i < c.size(); i++) {
            if (hueOrder.get(i) == null &&
                    //first hue must be successor of hue we whose successor node we are looking for
                    //or it must contain the formula if the node is the root
                    (i != 0 ||
                            (parent == null && parentHue == null && f.getFHues().contains(h)) ||
                            (parentHue != null && parentHue.getSuccessors(f.getAllHues()).contains(h)))) {
                statusPrint(TABLEAU, MAX, "Permuting: placing " + h.name + " at position " + i);
                hueOrder.put(i, h);

                //base case
                if (hueOrder.size() == c.size()) {
                    subSectionPrint(TABLEAU, SOME, "Fixing hue order");
                    Node n = addLeaf(parent, parentHue, c, hueOrder);
                    if (root == null) {
                        root = n;
                        statusPrint(TABLEAU, SOME, "(This is the new root)");
                    }
                    result = checkNode(n, level, checkedUpLinks, checkAll);
                    if (result == FAILURE) {
                        hueOrder.remove(i);
                        statusPrint(TABLEAU, MAX, "Hue permutations: removing " + h.name + " from position " + i);
                        removeNode(n);
                        continue PERMUTATION_LOOP;
                    } else {
                        result = SUCCESS;
                        break PERMUTATION_LOOP;
                    }

                    //recursion case
                } else {
                    switch (checkHuePermutations(parent, parentHue, level, checkedUpLinks, checkAll, c, permutationIndex + 1, hueOrder)) {
                        case SUCCESS:
                            result = SUCCESS;
                            break PERMUTATION_LOOP;
                        case FAILURE:
                            statusPrint(TABLEAU, MAX, "Hue permutations: removing " + h.name + " from position " + i);
                            hueOrder.remove(i);
                            continue PERMUTATION_LOOP;
                        default:
                    }
                }
            } else {
                //space is filled with other hue; continue to next space
                continue PERMUTATION_LOOP;
            }
            result = FAILURE;
        }
        return result;
    }

    public Node addLeaf(Node parent, Hue predecessorHue, Colour c, Map<Integer, Hue> hueOrder) {
        //TODO: check if huerx holds, throw error if doesn't
        Node newLeaf;
        if (parent != null) {
            int index = parent.zOrder.indexOf(predecessorHue);
            newLeaf = new Node(c, hueOrder, parent, "" + index);
            parent.successors.put(index, newLeaf);
        } else {
            newLeaf = new Node(c, hueOrder);
        }
        return newLeaf;
    }



}
