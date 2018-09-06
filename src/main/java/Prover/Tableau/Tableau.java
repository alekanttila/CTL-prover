package Prover.Tableau;

import Prover.Formula.*;
import Prover.Tableau.Pair.NodeHue;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.SOME;
import static Prover.StatusMessage.statusPrint;
import static Prover.Tableau.Pair.nH;
import static Prover.Tableau.Tableau.ExtendResult.FAILURE;
import static Prover.Tableau.Tableau.ExtendResult.SUCCESS;

public abstract class Tableau {
    protected Node root;
    public boolean multiUpLinks;
    protected int tableausBuilt = 0;
    protected int lgRuns = 0;
    protected int maxSteps = -1;
    protected final Formula f;
    public int maxBranchLength;

    public Tableau(Formula f) {
        this.f = f;
        this.maxBranchLength = 5;
    }

    public abstract ExtendResult solve();

    protected Node addLeaf(Node parent, Hue predecessorHue, Colour c, Hue firstHue) {
        Node newLeaf;
        if (parent != null) {
            int index = parent.zOrder.indexOf(predecessorHue);
            newLeaf = new Node(c, firstHue, parent, "" + index);
            parent.successors.put(index, nH(newLeaf, firstHue));
        } else {
            newLeaf = new Node(c, firstHue);
        }
        return newLeaf;
    }

    protected void removeNode(Node node) {
        if (node != root) {
            Node parent = node.ancestors.get(node.ancestors.size() - 2);
            parent.successors.values().remove(node.getStandardNH());
        } else {
            root = null;
        }
    }

    protected ExtendResult checkUpLinks(Node n, Hue hueToCheck, UpLinkTree checkedUpLinks) {
        ANCESTOR_LOOP:
        for (Node a : n.ancestors) {
            if (n.z.getSuccessors(f.getAllColours()).contains(a.z)) {
                if (multiUpLinks) {
                    for (Hue h : a.z) {
                        if (hueToCheck.getSuccessors(f.getAllHues()).contains(h)) {
                            addUpLink(n, hueToCheck, a, h);
                            statusPrint(TABLEAU, MAX, "Adding uplink to " + a.getName() + " " + h.name + " and initiating LG with");
                            statusPrint(TABLEAU, MAX, infoString());
                            lgRuns++;
                            if (LG.check(f, root)) {
                                tableausBuilt++;
                                statusPrint(TABLEAU, MAX, "LG OK");
                                checkedUpLinks.add(n.z, hueToCheck, nH(a,h));
                                return SUCCESS;
                            } else {
                                statusPrint(TABLEAU, MAX, "LG failed");
                                removeUpLink(n, a, h);
                                continue ANCESTOR_LOOP;
                            }
                        }
                    }
                } else {
                    if (hueToCheck.getSuccessors(f.getAllHues()).contains(a.zOrder.get(0))) {
                        addUpLink(n, hueToCheck, a, a.zOrder.get(0));
                        statusPrint(TABLEAU, MAX, "Adding uplink to " + a.getName() + " " + a.zOrder.get(0).name + " and initiating LG with");
                        statusPrint(TABLEAU, MAX, infoString());
                        lgRuns++;
                        if (LG.check(f, root)) {
                            tableausBuilt++;
                            statusPrint(TABLEAU, MAX, "LG OK");
                            if (this instanceof BreadthTableau) {
                                checkedUpLinks.add(new Pair(n.z, n.zOrder.get(0)), hueToCheck, a.getStandardNH());
                            } else if (this instanceof PermutationBreadthTableau) {
                                checkedUpLinks.add(n.zOrder, hueToCheck, a.getStandardNH());
                            } else {
                                checkedUpLinks.add(n.z, hueToCheck, a.getStandardNH());
                            }
                            return SUCCESS;
                        } else {
                            statusPrint(TABLEAU, MAX, "LG failed");
                            removeUpLink(n, a, a.zOrder.get(0));
                            continue ANCESTOR_LOOP;
                        }

                    }
                }
            }
        }
        statusPrint(TABLEAU, MAX,"All ancestors checked. No upLinks possible");
        if (this instanceof BreadthTableau) {
            checkedUpLinks.add(new Pair(n.z, n.zOrder.get(0)), hueToCheck, new UpLinkTree<Pair<Colour, Hue>>());
        } else if (this instanceof PermutationBreadthTableau) {
            checkedUpLinks.add(n.zOrder, hueToCheck, new UpLinkTree<List<Colour>>());
        } else {
            checkedUpLinks.add(n.z, hueToCheck, new UpLinkTree<Colour>());
        }
        return FAILURE;
    }


    //when multiuplink = true
    public void addUpLink(Node parent, Hue predecessor, Node child, Hue successor) {
        parent.successors.put(parent.zOrder.indexOf(predecessor), nH(child, successor));
    }

    public Node retrieveNodeByName(String name) {
        name = name.substring(1);
        Node result = root;
        for (int i = 0; i < name.length(); i++) {
            result = result.successors.get(Integer.parseInt(Character.toString(name.charAt(i)))).node();
        }
        return result;
    }

    //TODO: write use name and retrieve by name here because WHY??
    public void restoreUpLink(Node parent, Hue predecessor, NodeHue childNodeHue) {
        String childName = childNodeHue.node().getName();
        Node child = retrieveNodeByName(childName);
        if (multiUpLinks) {
            addUpLink(parent, predecessor, child, childNodeHue.hueAsHue());
        } else {
            addUpLink(parent, predecessor, child, child.zOrder.get(0));
        }
    }

    public void removeUpLink(Node parent, Node child, Hue successor) {
        parent.successors.remove(nH(child, successor));
    }

    protected boolean createDummies(Node n, ColourSet possibleChildColours) {
        boolean result = true;
        DUMMY_CREATION_LOOP:
        for (Hue predecessorHue : n.zOrder) {
            HueSet possibleSuccessorHues = predecessorHue.getSuccessors(f.getAllHues());
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


    public String fullInfoString() {
        return infoString(root, null) + "\nTableaus built: " + tableausBuilt + " LG runs: " + lgRuns;
    }

    public String infoString() {
        return infoString(root, null);
    }

    public String infoString(Node n, Set<Node> printedNodes) {
        if (n == null) {
            return "";
        }
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
        Iterator<NodeHue> i = n.successors.values().iterator();
        while (i.hasNext()) {
            NodeHue s = i.next();
            result = result + s.node().getName() + " ";
        }
        result = result + "\n";
        Iterator<NodeHue> i2 = n.successors.values().iterator();
        while (i2.hasNext()) {
            NodeHue sNH = i2.next();
            Node s = sNH.node();
            if (printedNodes.contains(s)) {
                continue;
            }
            result = result + infoString(s, printedNodes);
        }
        return result;
    }

    enum ExtendResult {
        SUCCESS, FAILURE, STEPS_COMPLETE;
        protected UpLinkTree upLinks;
        protected ConcurrentUpLinkTree cUpLinks;
    }
}
