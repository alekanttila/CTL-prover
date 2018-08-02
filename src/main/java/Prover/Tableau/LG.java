package Prover.Tableau;

import Prover.Formula.*;

import java.util.*;

import static Prover.Formula.Formula.Connective.*;
import static Prover.StatusMessage.Area.LG;
import static Prover.StatusMessage.Level.MAX;
import static Prover.StatusMessage.Level.SOME;
import static Prover.StatusMessage.statusPrint;

public class LG {

    //could construct the algorithm by recursing down the tree, but since the
    // tree loops back on itself, this would necessitate traversal checks
    //whenever any node is update; we use a single list of nodes instead
    //(and do the traversal check only in the initialization phase)
    //stored statically to avoid returning both of these in all methods;
    //we still have to pass them to make use of old values as we go
    static Formula f;

    public static boolean check(Formula phi, Node root) {
        //reset static members
        f = phi;
        FormulaSet closure = f.getClosure();
        PartialStructure currentStructure = new PartialStructure();
        currentStructure.initialize(root);

        for (Formula g : closure) {
            statusPrint(LG, SOME, "LG: updating with: ");
            statusPrint(LG, SOME, g.sugarString(phi.getFormulaNames()));
            switch (g.getC()) {
                case ATOM:
                case TRUE:
                    //TODO: write about TRUE in report
                    currentStructure = updateAtom(g, currentStructure);
                    break;
                case NOT:
                    if (g.getSf1().getC() == NOT) {
                        currentStructure = updateNot(g, currentStructure);
                    }
                    break;
                case AND:
                    currentStructure = updateAnd(g, currentStructure);
                    break;
                case X:
                    currentStructure = updateX(g, currentStructure);
                    break;
                case A:
                    currentStructure = updateA(g, currentStructure);
                    break;
                case U:
                    currentStructure = updateU(g, currentStructure);
                    break;
                    //TODO: add true to all hues?
                case FALSE:
                default:
                    //TODO:ERROR
            }
            statusPrint(LG, SOME, currentStructure.sugarString(phi.getFormulaNames()));
        }
        //TODO: subset checker
        boolean result = checkLabels(currentStructure);
        return result;
    }
    private static boolean checkLabels(PartialStructure s) {
        boolean result = true;
        //statusPrint("LG final check. Partial Structure:");
        //statusPrint(s.sugarString(f.getFormulaNames()));

        for (Node n: s.nodes()) {
            statusPrint(LG, MAX, "Actual colours");
            statusPrint(LG, MAX, n.getName() + n.z.sugarString(0, f.getFormulaNames()));
            //TODO: write method for getting colours out of treeset formulasets
            HueSet hS = new HueSet();
            for (FormulaSet h : s.getNodeColour(n) ) {
                hS.add(new Hue(h, 0));
            }
            Colour c  = new Colour(hS, 0);
            //statusPrint(n.getName() + c.sugarString(0, f.getFormulaNames()));
            if (!n.z.equals(c)) {
                //System.out.println("CHCHCHCHCH");
                //System.out.println(n.getName());
                //for (FormulaSet h : s.getNodeColour(n)) {
                //   statusPrint(h.sugarString(f.getFormulaNames()));
                //}
                result = false;
                //break;
            }
        }
        //System.out.println(result);
        return result;
    }

    private static PartialStructure updateAtom(Formula atomOrT, PartialStructure oldS) {
        PartialStructure newS = new PartialStructure(oldS);
        //TODO look up arraylist, hashmap retrieval complexity
        //TODO: look up getindex complexity; change resetCOlour?
        //use one index for both nodes and hues so we do not have to use indexOf (or hashmap lookup)
        PartialStructure.ExtensionData updatedHues = new PartialStructure.ExtensionData();
        for (Node n : oldS.nodes()) {
            Formula newF;
            boolean add;
            if (atomOrT.getC() == ATOM) {
                add = n.z.containsF(atomOrT);
            } else {
                add = true;
            }
            if (add) {
                newF = atomOrT;
            } else {
                newF = new Formula(atomOrT, NOT);
            }
            for (FormulaSet h : oldS.pC().get(n)) {
                newS.addHue(n, h, newF, updatedHues);
            }
        }
        newS.simpleSuccessorUpdate(oldS, updatedHues);
        return newS;
    }

    //TODO: write correction in report THINK FIRST
    private static PartialStructure updateNot(Formula notNotF, PartialStructure oldS) {
        PartialStructure newS = new PartialStructure(oldS);
        PartialStructure.ExtensionData updatedHues = new PartialStructure.ExtensionData();
        for (Node n : oldS.nodes()) {
            for (FormulaSet h : oldS.pC().get(n)) {
                Formula newF;
                if (h.contains(notNotF.getSf1().getSf1())) {
                    newF = notNotF;
                } else {
                    newF = null;
                }
                newS.addHue(n, h, newF, updatedHues);
            }
        }
        newS.simpleSuccessorUpdate(oldS, updatedHues);
        return newS;
    }

    private static PartialStructure updateAnd(Formula fAndG, PartialStructure oldS) {
        PartialStructure newS = new PartialStructure(oldS);
        PartialStructure.ExtensionData updatedHues = new PartialStructure.ExtensionData();
        for (Node n : oldS.nodes()) {
            for (FormulaSet h : oldS.pC().get(n)) {
                Formula newF;
                if (h.contains(fAndG.getSf1()) && h.contains(fAndG.getSf2())) {
                    newF = fAndG;
                } else {
                    newF = f.getClosure().getReference(new Formula(fAndG, NOT));
                }
                newS.addHue(n, h, newF, updatedHues);
            }
        }
        newS.simpleSuccessorUpdate(oldS, updatedHues);
        return newS;
    }

    private static PartialStructure updateX(Formula xF, PartialStructure oldS) {
        PartialStructure newS = new PartialStructure(oldS);
        PartialStructure.ExtensionData positiveExtensions = new PartialStructure.ExtensionData();
        PartialStructure.ExtensionData negativeExtensions = new PartialStructure.ExtensionData();
        for (Node nodeToCheck : oldS.nodes()) {
            for (FormulaSet hueToCheck : oldS.pC().get(nodeToCheck)) {
                //these will eventually be entered into the ExtensionData structures, but since we check
                //them many times, we save on querying costs by creating separate booleans
                boolean currentPositiveExtension = false;
                boolean currentNegativeExtension = false;
                if (nodeToCheck.isLeaf()) {
                    for (FormulaSet endHue : nodeToCheck.z) {
                        if (endHue.containsAll(hueToCheck)) {
                            if (endHue.contains(xF)) {
                                currentPositiveExtension = true;
                            } else {
                                currentNegativeExtension = true;
                            }
                        }
                        //break if both already made true
                        if (currentPositiveExtension == true && currentNegativeExtension== true) {
                            break;
                        }
                    }
                } else {
                    for (Node possibleSuccessor : oldS.nodes()) {
                        for (FormulaSet possibleHueSuccessor : oldS.pC().get(possibleSuccessor)) {
                            if (oldS.isSuccessor(nodeToCheck, hueToCheck, possibleSuccessor, possibleHueSuccessor)) {
                                if (possibleHueSuccessor.contains(xF.getSf1())) {
                                    currentPositiveExtension = true;
                                } else {
                                    currentNegativeExtension = true;
                                }
                            }
                            if (currentPositiveExtension == true && currentNegativeExtension== true) {
                                break;
                            }
                        }
                    }
                }
                if (currentPositiveExtension == true) {
                    newS.addHue(nodeToCheck, hueToCheck, xF, positiveExtensions);
                    positiveExtensions.setBoolean(nodeToCheck, hueToCheck, true);
                }
                if (currentNegativeExtension == true) {
                    newS.addHue(nodeToCheck, hueToCheck, new Formula(xF, NOT), negativeExtensions);
                    negativeExtensions.setBoolean(nodeToCheck, hueToCheck, true);
                }
            }
        }
        newS.initializeSuccessorSets();
        for (Node n1 : oldS.nodes()) {
            for (FormulaSet h1 : oldS.pC().get(n1)) {
                for (Node n2 : oldS.nodes()) {
                    for (FormulaSet h2 : oldS.pC().get(n2)) {
                        if (oldS.isSuccessor(n1, h1, n2, h2)) {
                            if (h2.contains(xF.getSf1())) {
                                if (positiveExtensions.getBoolean(n2, h2)) {
                                    newS.addSuccessor(positiveExtensions, n1, h1, positiveExtensions, n2, h2);
                                }
                                if (negativeExtensions.getBoolean(n2, h2)) {
                                    newS.addSuccessor(positiveExtensions, n1, h1, negativeExtensions, n2, h2);
                                }
                            } else {
                                if (positiveExtensions.getBoolean(n2, h2)) {
                                    newS.addSuccessor(negativeExtensions, n1, h1, positiveExtensions, n2, h2);
                                }
                                if (negativeExtensions.getBoolean(n2, h2)) {
                                    newS.addSuccessor(negativeExtensions, n1, h1, negativeExtensions, n2, h2);
                                }

                            }
                        }
                    }
                }
            }
        }
        return newS;
    }

    private static PartialStructure updateA(Formula aF, PartialStructure oldS) {
        PartialStructure newS = new PartialStructure(oldS);
        PartialStructure.ExtensionData updatedHues = new PartialStructure.ExtensionData();
        for (Node n : oldS.nodes()) {
            boolean add = true;
            for (FormulaSet h : oldS.pC().get(n)) {
                if (!h.contains(aF.getSf1())) {
                    add = false;
                    break;
                }
            }
            Formula newF;
            if (add) {
                newF = aF;
            } else {
                newF = new Formula(aF, NOT);
            }
            for (FormulaSet h : oldS.pC().get(n)) {
                newS.addHue(n, h, newF, updatedHues);
            }
        }
        newS.simpleSuccessorUpdate(oldS, updatedHues);
        return newS;
    }

    private static PartialStructure updateU(Formula fUG, PartialStructure oldS) {
        PartialStructure newS = new PartialStructure(oldS);
        PartialStructure.ExtensionData positiveExtensions = addU(fUG, oldS, newS);
        PartialStructure.ExtensionData negativeExtensions = addNotU(fUG, oldS, newS);
        newS = uSuccessorUpdate(fUG, oldS, newS, positiveExtensions, negativeExtensions);
        return newS;
    }

    private static PartialStructure uSuccessorUpdate(Formula phiUChi, PartialStructure oldS, PartialStructure newS, PartialStructure.ExtensionData positiveExtensions, PartialStructure.ExtensionData negativeExtensions) {
        newS.initializeSuccessorSets();
        Formula phi = phiUChi.getSf1();
        Formula chi = phiUChi.getSf2();
        for (Node n1 : oldS.nodes()) {
            for (FormulaSet h1 : oldS.pC().get(n1)) {
                for (Node n2 : oldS.nodes()) {
                    for (FormulaSet h2 : oldS.pC().get(n2)) {
                        if (oldS.isSuccessor(n1, h1, n2, h2)) {
                            if (positiveExtensions.getBoolean(n1, h1)) {
                                if (positiveExtensions.getBoolean(n2, h2)) {
                                    newS.addSuccessor(positiveExtensions, n1, h1, positiveExtensions, n2, h2);
                                }
                                if (negativeExtensions.getBoolean(n2, h2) && h1.contains(chi)) {
                                    newS.addSuccessor(positiveExtensions, n1, h1, negativeExtensions, n2, h2);
                                }
                            }
                            if (negativeExtensions.getBoolean(n1, h1)) {
                                if (positiveExtensions.getBoolean(n2, h2) && h1.contains(new Formula(phi, NOT))) {
                                    newS.addSuccessor(negativeExtensions, n1, h1, positiveExtensions, n2, h2);
                                }
                                if (negativeExtensions.getBoolean(n2, h2)) {
                                    newS.addSuccessor(negativeExtensions, n1, h1, negativeExtensions, n2, h2);
                                }
                            }
                        }
                    }
                }
            }
        }
        return newS;
    }

    //TODO: initialize formulas at the start of these for cleare reference
    //TODO: change formula names to alpha, beta (phi chi)
    private static PartialStructure.ExtensionData addNotU(Formula phiUChi, PartialStructure oldS, PartialStructure newS) {
        Formula phi = phiUChi.getSf1();
        Formula chi = phiUChi.getSf2();
        Formula notPhiUChi = new Formula(phiUChi, NOT);
        PartialStructure.ExtensionData negativeExtensions = new PartialStructure.ExtensionData();
        for (Node nodeToCheck : oldS.nodes()) {
            HUE_LOOP:
            for (FormulaSet hueToCheck : oldS.pC().get(nodeToCheck)) {
                negativeExtensions.setBoolean(nodeToCheck, hueToCheck, false);
                if (hueToCheck.contains(chi)) {
                    //statusPrint("found " + chi.sugarString() + " in " + nodeToCheck.getName() + " " + hueToCheck.sugarString());
                    //statusPrint("no neg extension");
                    break HUE_LOOP;
                } else if (hueToCheck.contains(new Formula(phi, NOT))) {
                    //statusPrint("found neg of " + phi.sugarString() + " in " + nodeToCheck.getName() + " " + hueToCheck.sugarString());
                    //statusPrint("neg extension");
                    negativeExtensions.setBoolean(nodeToCheck, hueToCheck, true);
                    newS.addHue(nodeToCheck, hueToCheck, notPhiUChi, negativeExtensions);
                    break HUE_LOOP;
                } else if (nodeToCheck.isLeaf()) {
                    for (FormulaSet endHue : nodeToCheck.z) {
                        if (endHue.containsAll(hueToCheck)) {
                            if (endHue.contains(notPhiUChi)) {
                                //statusPrint("found " + notPhiUChi.sugarString() + " in " + nodeToCheck.getName() + " " + hueToCheck.sugarString());
                                //statusPrint("leaf neg extension");
                                negativeExtensions.setBoolean(nodeToCheck, hueToCheck, true);
                                newS.addHue(nodeToCheck, hueToCheck, notPhiUChi, negativeExtensions);
                                break HUE_LOOP;
                            }
                        }
                    }
                } else if (fulfillingLoopCheck(new Formula(chi, NOT), oldS, nodeToCheck, hueToCheck)) {
                    //statusPrint("fulfilling loop via neg of " + chi.sugarString() + " thru " + nodeToCheck.getName() + " " + hueToCheck.sugarString());
                    //statusPrint("neg extension");
                    negativeExtensions.setBoolean(nodeToCheck, hueToCheck, true);
                    newS.addHue(nodeToCheck, hueToCheck, notPhiUChi, negativeExtensions);
                    break HUE_LOOP;
                }
            }
        }
        boolean change = true;
        while (change) {
            change = false;
            for (Node n1 : oldS.nodes()) {
                HUE_LOOP:
                for (FormulaSet h1 : oldS.pC().get(n1)) {
                    if (h1.contains(new Formula(chi, NOT)) && !negativeExtensions.getBoolean(n1, h1)) {
                        for (Node n2 : oldS.nodes()) {
                            for (FormulaSet h2 : oldS.pC().get(n2)) {
                                if (oldS.isSuccessor(n1, h1, n2, h2) && negativeExtensions.getBoolean(n2, h2)) {
                                    change = true;
                                    newS.addHue(n1, h1, notPhiUChi, negativeExtensions);
                                    negativeExtensions.setBoolean(n1, h1, true);
                                    break HUE_LOOP;
                                }
                            }
                        }
                    }
                }
            }
        }
        return negativeExtensions;
    }

    //TODO: check z or zorder in all these, change to zorder?
    private static boolean fulfillingLoopCheck(Formula phi, PartialStructure oldS, Node nodeToCheck, FormulaSet hueToCheck) {
        int count = 0;
        List<Formula> eventualities = new ArrayList<Formula>();
        for (Formula chi : hueToCheck) {
            if (chi.getC() == U) {
                count++;
                eventualities.add(chi.getSf2());
            }
        }
        //System.out.println("eventualities " + eventualities);
        Map<Pair.NodeHue, Integer> eventualityCount = new HashMap<Pair.NodeHue, Integer>();
        for (Node n : oldS.nodes()) {
            for (FormulaSet h : oldS.pC().get(n)) {
                eventualityCount.put(nH(n, h), -1);
            }
        }
        eventualityCount.put(nH(nodeToCheck, hueToCheck), 0);
        boolean loop = false;
        boolean change = true;
        while (change) {
            change = false;
            for (Node n1 : oldS.nodes()) {
                for (FormulaSet h1 : oldS.pC().get(n1)) {
                    for (Node n2 : oldS.nodes()) {
                        for (FormulaSet h2 : oldS.pC().get(n2)) {
                            if (oldS.isSuccessor(n1, h1, n2, h2) && h1.contains(phi)) {
                                if (eventualityCount.get(nH(n1, h1)) < eventualityCount.get(nH(n2, h2))) {
                                    eventualityCount.put(nH(n1, h1), eventualityCount.get(nH(n2, h2)));
                                    //System.out.println("putting " + n1.getName() + h1.sugarString() + eventualityCount.get(nH(n1, h1)));
                                    change = true;
                                    if (n1 == nodeToCheck && h1 == hueToCheck) {
                                        loop = true;
                                    }
                                } else if (n1 == nodeToCheck && h1 == hueToCheck &&
                                        eventualityCount.get(nH(n1, h1)) == eventualityCount.get(nH(n2, h2)) && !loop) {
                                    //System.out.println("loop w no eventualities");
                                    change = true;
                                    loop = true;
                                }
                            }
                        }
                    }
                    for (int i = 1; i < (count + 1); i++) {
                        if (eventualityCount.get(nH(n1, h1)) == i - 1 && h1.contains(eventualities.get(i - 1))) {
                            eventualityCount.put(nH(n1, h1), i);
                            //System.out.println("putting2 " + n1.getName() + h1.sugarString() + eventualityCount.get(nH(n1,h1)));
                            change = true;
                        }
                    }
                }
            }
        }
        boolean result;
        if (loop == true && eventualityCount.get(nH(nodeToCheck, hueToCheck)) == count) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    private static PartialStructure.ExtensionData addU(Formula fUG, PartialStructure oldS, PartialStructure newS) {
        PartialStructure.ExtensionData positiveExtensions = new PartialStructure.ExtensionData();
        for (Node n : oldS.nodes()) {
            for (FormulaSet h : oldS.pC().get(n)) {
                positiveExtensions.setBoolean(n, h, false);
            }
        }
        boolean change = true;
        while (change == true) {
            change = false;
            for (Node nodeToCheck : oldS.nodes()) {
                for (FormulaSet hueToCheck : oldS.pC().get(nodeToCheck)) {
                    if (!positiveExtensions.getBoolean(nodeToCheck, hueToCheck)) {
                        boolean add = false;
                        if (hueToCheck.contains(fUG.getSf2())) {
                            add = true;
                            //statusPrint(nodeToCheck.getName() + " " + hueToCheck.sugarString() + " contains " + fUG.getSf2().sugarString());
                        } else if (nodeToCheck.isLeaf()) {
                            for (Hue endHue : nodeToCheck.z) {
                                if (endHue.containsAll(hueToCheck)) {
                                    if (endHue.contains(fUG)) {
                                        //statusPrint("leaf " + nodeToCheck.getName() + " " + hueToCheck.sugarString() + " contains " + fUG.getSf2().sugarString());
                                        add = true;
                                        break;
                                    }
                                }
                            }
                        } else if (hueToCheck.contains(fUG.getSf1())){
                            POTENTIAL_SUCCESSOR_LOOP:
                            for (Node potentialSuccessor : oldS.nodes()) {
                                for (FormulaSet potentialHueSuccessor : oldS.pC().get(potentialSuccessor)) {
                                    if (oldS.isSuccessor(nodeToCheck, hueToCheck, potentialSuccessor, potentialHueSuccessor) &&
                                            positiveExtensions.getBoolean(potentialSuccessor, potentialHueSuccessor)) {
                                        //statusPrint(nodeToCheck.getName() + " " + hueToCheck.sugarString() + " precededs " + potentialSuccessor.getName() + " " + potentialHueSuccessor.sugarString());
                                        add = true;
                                        break POTENTIAL_SUCCESSOR_LOOP;
                                    }
                                }
                            }
                        }
                        if (add) {
                            positiveExtensions.setBoolean(nodeToCheck, hueToCheck, true);
                            newS.addHue(nodeToCheck, hueToCheck, fUG, positiveExtensions);
                            change = true;
                        }
                    }
                }
            }
        }
        return  positiveExtensions;
    }

    protected static Pair.NodeHue nH(Node n, FormulaSet h) {
        return new Pair.NodeHue(n, h);
    }

    protected static FormulaSet getEmptyHue() {
        FormulaSet empty = new FormulaSet();
        return empty;
    }

    private static TreeSet<FormulaSet> getEmptyColour() {
        TreeSet<FormulaSet> onlyEmpty = new TreeSet<FormulaSet>();
        onlyEmpty.add(getEmptyHue());
        return onlyEmpty;
    }

}
