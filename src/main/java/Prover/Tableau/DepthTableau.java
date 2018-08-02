package Prover.Tableau;

import Prover.Formula.*;

import static Prover.Tableau.ExtendResult.SUCCESS;

public class DepthTableau extends Tableau {

    public DepthTableau(Formula f) {
        super(f);
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

    //TODO: think: can it be the case that given a certain hue order for a node, we can uplink a hue, and find no upLinks for the rest of the hues, but with a different order, all the hues would eventually uplink?

    private ExtendResult extend(Node n) {
        ExtendResult result = null;
        if (maxSteps == -1 || steps <= maxSteps) {
            if (upLinkCheck(n)) {
                steps++;
                result = SUCCESS;
            } else if (n.branchLength <= maxBranchLength) {
                ColourSet successorColours = n.z.getSuccessors(f.getAllColours());
                A:
                //iterating through zOrder in key order
                for (Hue h : n.zOrder) {
                    HueSet successorHues = h.getSuccessors(f.getAllHues());
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

    public ExtendResult stepSolve(int stepNumber) {
        maxSteps = stepNumber;
        return solve();
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
}
