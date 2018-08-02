package Prover.Tableau;

import Prover.Formula.*;
import com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static Prover.StatusMessage.Area.TABLEAU;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.SOME;
import static Prover.StatusMessage.statusPrint;

public abstract class Tableau {
    protected Node root;
    protected int tableausBuilt = 0;
    protected int lgRuns = 0;
    protected int maxSteps = -1;
    protected final Formula f;
    protected final int maxBranchLength;

    public Tableau(Formula f) {
        //TODO: check result set
        this.f = f;
        //TODO: remove duplication (since these in result set)???
        this.maxBranchLength = 4;//TODO: replace
    }

    public abstract ExtendResult solve();

    protected Node addLeaf(Node parent, Hue predecessorHue, Colour c, Hue firstHue) {
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

    protected void removeNode(Node node) {
        if (node != root) {
            Node parent = node.ancestors.get(node.ancestors.size() - 2);
            parent.successors.values().remove(node);
        } else {
            root = null;
        }
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


}
